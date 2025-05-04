package com.minedu.gob.pe.mssigetip.service;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacpp.Loader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

public class FacialRecognitionService {

    static {
        // Cargar automáticamente las librerías nativas necesarias (¡no uses System.load!)
        Loader.load(opencv_objdetect.class);
        Loader.load(opencv_face.class);
        System.out.println("OpenCV (JavaCV) cargado automáticamente");
    }

    public static boolean authenticate(String registeredImagePath, String newImagePath) throws URISyntaxException {
        // Cargar clasificador desde resources
        File xmlFile = new File(Objects.requireNonNull(
                FacialRecognitionService.class.getClassLoader().getResource("opencv/haarcascade_frontalface_default.xml")
        ).toURI());

        CascadeClassifier faceDetector = new CascadeClassifier(xmlFile.getAbsolutePath());
        if (faceDetector.empty()) {
            throw new RuntimeException("El clasificador no se cargó correctamente.");
        }

        // Cargar imágenes
        Mat img1 = opencv_imgcodecs.imread(registeredImagePath, opencv_imgcodecs.IMREAD_GRAYSCALE);
        Mat img2 = opencv_imgcodecs.imread(newImagePath, opencv_imgcodecs.IMREAD_GRAYSCALE);

        if (img1.empty() || img2.empty()) {
            System.err.println("No se pudieron cargar las imágenes.");
            return false;
        }

        // Detectar rostros
        RectVector faces1 = new RectVector();
        faceDetector.detectMultiScale(img1, faces1);

        RectVector faces2 = new RectVector();
        faceDetector.detectMultiScale(img2, faces2);

        if (faces1.empty() || faces2.empty()) {
            System.out.println("No se detectaron rostros en una o ambas imágenes.");
            return false;
        }

        // Recortar rostros
        Rect r1 = faces1.get(0);
        Rect r2 = faces2.get(0);
        Mat face1 = new Mat(img1, r1);
        Mat face2 = new Mat(img2, r2);

        // Crear y entrenar el reconocedor
        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        Mat labels = new Mat(1, 1, opencv_core.CV_32SC1);
        labels.ptr(0).putInt(1);

        MatVector trainingFaces = new MatVector(1);
        trainingFaces.put(0, face1);

        recognizer.train(trainingFaces, labels);

        IntPointer predictedLabel = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        recognizer.predict(face2, predictedLabel, confidence);

        System.out.println("Etiqueta predicha: " + predictedLabel.get());
        System.out.println("Confianza: " + confidence.get());

        return predictedLabel.get() == 1 && confidence.get() < 50;
    }
}
