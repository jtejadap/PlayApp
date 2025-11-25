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
        InputStream is = getClass().getResourceAsStream("/weka/modelo_ventas_m5p.model");
        ObjectInputStream ois = new ObjectInputStream(is);
        modelo = (Classifier) ois.readObject();
        ois.close();
        log.info("Modelo WEKA cargado correctamente.");
    }

    private void cargarEstructura() throws Exception {
        InputStream is = getClass().getResourceAsStream("/weka/dataset_ventas.arff");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        estructura = new Instances(reader);
        estructura.setClassIndex(estructura.numAttributes() - 1);

        reader.close();
        log.info("Estructura ARFF cargada correctamente.");
    }

    public double predecirVenta(
            String fecha,
            int diaSemana,
            String producto,
            String categoria,
            double precio,
            int cantidadVendida,
            String clima,
            String temporada
    ) {
        try {
            Instance instancia = new DenseInstance(estructura.numAttributes());
            instancia.setDataset(estructura);

            instancia.setValue(0, normalize(fecha));
            instancia.setValue(1, diaSemana);
            instancia.setValue(2, normalize(producto));
            instancia.setValue(3, normalize(categoria));
            instancia.setValue(4, precio);
            instancia.setValue(5, cantidadVendida);
            instancia.setValue(6, normalize(clima));
            instancia.setValue(7, normalize(temporada));

            return modelo.classifyInstance(instancia);

        } catch (Exception e) {
            log.error("Error al predecir venta", e);
            throw new RuntimeException("No se pudo realizar la predicción.");
        }
    }

    String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
