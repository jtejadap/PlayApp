package com.proyecto.PlayApp.service;

import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

@Service
@Slf4j
public class WekaPredictionService {

    private Classifier modelo;
    private Instances estructura;

    public WekaPredictionService() {
        try {
            cargarModelo();
            cargarEstructura();
        } catch (Exception e) {
            log.error("Error cargando modelo o estructura WEKA", e);
        }
    }

    private void cargarModelo() throws Exception {
        InputStream is = getClass().getResourceAsStream("/weka/playapp.model");
        ObjectInputStream ois = new ObjectInputStream(is);
        modelo = (Classifier) ois.readObject();
        ois.close();
    }

    private void cargarEstructura() throws Exception {
        InputStream is = getClass().getResourceAsStream("/weka/dataset_ventas.arff");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        estructura = new Instances(reader);
        estructura.setClassIndex(estructura.numAttributes() - 1);
        reader.close();
    }

    public double predecirVenta(
            String diaSemana,
            String producto,
            String categoria,
            int precio,
            String clima,
            String temporada
    ) {

        try {
        Instance instancia = new DenseInstance(estructura.numAttributes());
        instancia.setDataset(estructura);

        instancia.setValue(0, diaSemana);
        instancia.setValue(1, producto);
        instancia.setValue(2, categoria);
        instancia.setValue(3, precio);
        instancia.setValue(4, clima);
        instancia.setValue(5, temporada);

        double resultado = modelo.classifyInstance(instancia);

        return resultado;

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("No se pudo realizar la predicción.");
    }

    }
}
