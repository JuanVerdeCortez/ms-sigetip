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
import org.bytedeco.opencv.opencv_core.Mat;
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

/**
 * Servicio de autenticación facial:
 * 1) Verifica que ambas imágenes contengan al menos un rostro.
 * 2) Extrae el primer rostro de cada imagen.
 * 3) Compara las regiones faciales usando Template Matching.
 */
@Service
public class FacialRecognitionByteService {

    private static final Logger logger = LoggerFactory.getLogger(FacialRecognitionByteService.class);

    @Autowired
    private UserRepository userRepository;

    private static final CascadeClassifier faceDetector;
    static {
        Loader.load(opencv_core.class);
        Loader.load(opencv_imgcodecs.class);
        Loader.load(opencv_imgproc.class);
        Loader.load(opencv_objdetect.class);
        try {
            URI uri = Objects.requireNonNull(
                    FacialRecognitionByteService.class.getClassLoader()
                            .getResource("opencv/haarcascade_frontalface_default.xml")
            ).toURI();
            faceDetector = new CascadeClassifier(new File(uri).getAbsolutePath());
            if (faceDetector.empty()) {
                throw new RuntimeException("No se pudo cargar Haar Cascade");
            }
            logger.info("@SLF4J Haar Cascade cargado correctamente");
        } catch (Exception e) {
            logger.error("@SLF4J Error cargando Haar Cascade", e);
            throw new RuntimeException(e);
        }
    }

    private static final int STANDARD_SIZE = 200;
    private static final double MATCH_THRESHOLD = 0.5;

    /**
     * Comprueba presencia de rostros y compara si pertenecen a la misma persona.
     */
    public boolean compareFaces(byte[] registeredPhoto, byte[] inputPhoto) {
        logger.info("@SLF4J compareFaces iniciado");

        // 1) Verificar detección de rostros
        int countReg = countFaces(registeredPhoto);
        int countInp = countFaces(inputPhoto);
        if (countReg == 0 || countInp == 0) {
            logger.warn("@SLF4J Falta rostro: registeredFaces={}, inputFaces={}", countReg, countInp);
            return false;
        }
        logger.info("@SLF4J Rostros detectados: registered={}, input={}", countReg, countInp);

        // 2) Extraer primer rostro de cada imagen
        Mat faceReg = extractFirstFace(registeredPhoto);
        Mat faceInp = extractFirstFace(inputPhoto);
        if (faceReg == null || faceInp == null) {
            logger.warn("@SLF4J Error extrayendo regiones faciales");
            return false;
        }
        logger.info("@SLF4J Regiones faciales extraídas tamaño: {}x{} y {}x{}",
                faceReg.cols(), faceReg.rows(), faceInp.cols(), faceInp.rows());

        // 3) Comparar con Template Matching
        double similarity = matchTemplate(faceReg, faceInp);
        boolean match = similarity >= MATCH_THRESHOLD;
        logger.info("@SLF4J Similitud facial: {}, threshold={}, match={}", similarity, MATCH_THRESHOLD, match);
        return match;
    }

    // Cuenta cuántos rostros detecta una imagen
    private int countFaces(byte[] photo) {
        Mat gray = decodeToGray(photo);
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces, 1.1, 5,
                opencv_objdetect.CASCADE_SCALE_IMAGE,
                new Size(gray.cols()/5, gray.rows()/5), new Size());
        return (int) faces.size();
    }

    // Extrae y normaliza el primer rostro detectado
    private Mat extractFirstFace(byte[] photo) {
        Mat gray = decodeToGray(photo);
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces, 1.1, 5,
                opencv_objdetect.CASCADE_SCALE_IMAGE,
                new Size(gray.cols()/5, gray.rows()/5), new Size());
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

    // Aplica matchTemplate y devuelve la mejor correlación
    private double matchTemplate(Mat template, Mat image) {
        int resultCols = image.cols() - template.cols() + 1;
        int resultRows = image.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, opencv_core.CV_32FC1);
        opencv_imgproc.matchTemplate(image, template, result, opencv_imgproc.TM_CCOEFF_NORMED);
        DoublePointer minVal = new DoublePointer(1);
        DoublePointer maxVal = new DoublePointer(1);
        opencv_core.minMaxLoc(result, minVal, maxVal, null, null, null);
        return maxVal.get(0);
    }

    // Decodifica bytes en Mat de escala de grises
    private Mat decodeToGray(byte[] bytes) {
        Mat buf = new Mat(1, bytes.length, opencv_core.CV_8UC1, new BytePointer(bytes));
        return opencv_imgcodecs.imdecode(buf, opencv_imgcodecs.IMREAD_GRAYSCALE);
    }

    /** Recupera foto registrada desde la base de datos. */
    public byte[] obtenerFoto(String username) {
        return userRepository.findByUsername(username)
                .map(User::getFoto)
                .orElse(null);
    }
}
