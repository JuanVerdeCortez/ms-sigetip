package com.minedu.gob.pe.mssigetip.service;

import com.minedu.gob.pe.mssigetip.infra.repository.UserRepository;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio de autenticación facial:
 * 1) Verifica que ambas imágenes contengan al menos un rostro.
 * 2) Extrae y normaliza el primer rostro de cada imagen.
 * 3) Compara usando LBPHFaceRecognizer de OpenCV.
 */
@Service
public class FacialRecognitionByteService {

    private static final Logger logger = LoggerFactory.getLogger(FacialRecognitionByteService.class);

    @Autowired
    private UserRepository userRepository;

    private static final CascadeClassifier faceDetector;
    static {
        // Carga de librerías nativas
        Loader.load(opencv_core.class);
        Loader.load(opencv_imgcodecs.class);
        Loader.load(opencv_imgproc.class);
        Loader.load(opencv_objdetect.class);
        Loader.load(opencv_face.class);
        try {
            URI uri = Objects.requireNonNull(
                    FacialRecognitionByteService.class.getClassLoader()
                            .getResource("opencv/haarcascade_frontalface_default.xml")
            ).toURI();
            faceDetector = new CascadeClassifier(new File(uri).getAbsolutePath());
            if (faceDetector.empty()) {
                throw new RuntimeException("No se pudo cargar Haar Cascade");
            }
            logger.info("Haar Cascade cargado correctamente");
        } catch (Exception e) {
            logger.error("Error cargando Haar Cascade", e);
            throw new RuntimeException(e);
        }
    }

    private static final int STANDARD_SIZE = 200;
    private static final double LBPH_CONFIDENCE_THRESHOLD = 60.0;

    /**
     * Comprueba presencia de rostros y compara si pertenecen a la misma persona.
     */
    public boolean compareFaces(byte[] registeredPhoto, byte[] inputPhoto) {
        logger.info("🔎 compareFaces iniciado");


        int countReg = countFaces(registeredPhoto);
        int countInp = countFaces(inputPhoto);
        if (countReg == 0 || countInp == 0) {
            logger.warn("Falta rostro: registeredFaces={}, inputFaces={}", countReg, countInp);
            return false;
        }
        logger.info("👥 Rostros detectados: registered={}, input={}", countReg, countInp);

        // 2) Extraer y normalizar primer rostro
        Mat faceReg = extractFirstFace(registeredPhoto);
        Mat faceInp = extractFirstFace(inputPhoto);
        if (faceReg == null || faceInp == null) {
            logger.warn("Error extrayendo regiones faciales");
            return false;
        }
        logger.info(" Regiones faciales extraídas: {}x{} y {}x{}",
                faceReg.cols(), faceReg.rows(), faceInp.cols(), faceInp.rows());

        // 3) Entrenar y predecir con LBPH
        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        MatVector training = new MatVector(1);
        training.put(0, faceReg);
        Mat labels = new Mat(1, 1, opencv_core.CV_32SC1);
        labels.ptr(0).putInt(1);
        recognizer.train(training, labels);

        IntPointer predictedLabel = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        recognizer.predict(faceInp, predictedLabel, confidence);

        logger.info(" Etiqueta predicha: {}, confianza: {}", predictedLabel.get(), confidence.get());

        // Coincide si la etiqueta es 1 y la confianza está por debajo del umbral
        return predictedLabel.get() == 1 && confidence.get() < LBPH_CONFIDENCE_THRESHOLD;
    }

    private int countFaces(byte[] photo) {
        Mat gray = decodeToGray(photo);
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces, 1.1, 5,
                opencv_objdetect.CASCADE_SCALE_IMAGE,
                new Size(gray.cols()/5, gray.rows()/5),
                new Size());
        return (int) faces.size();
    }


    private Mat extractFirstFace(byte[] photo) {
        Mat gray = decodeToGray(photo);
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces, 1.1, 5,
                opencv_objdetect.CASCADE_SCALE_IMAGE,
                new Size(gray.cols()/5, gray.rows()/5),
                new Size());
        if (faces.empty()) {
            return null;
        }
        Rect r = faces.get(0);
        Mat face = new Mat(gray, r);
        Mat normFace = new Mat();
        opencv_imgproc.resize(face, normFace, new Size(STANDARD_SIZE, STANDARD_SIZE));
        opencv_imgproc.equalizeHist(normFace, normFace);
        return normFace;
    }


    private Mat decodeToGray(byte[] bytes) {
        Mat buf = new Mat(1, bytes.length, opencv_core.CV_8UC1, new BytePointer(bytes));
        return opencv_imgcodecs.imdecode(buf, opencv_imgcodecs.IMREAD_GRAYSCALE);
    }

    /** Recupera foto registrada desde la base de datos. */
    public byte[] obtenerFoto(String username) {
        return userRepository.findByUsername(username)
                .map(User::getFoto).orElseThrow();
    }
}
