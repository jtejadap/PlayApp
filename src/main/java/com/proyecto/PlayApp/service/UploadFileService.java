package com.proyecto.PlayApp.service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {

    private final String CARPETA = "images//"; // Define la carpeta para guardar las imágenes.

    // Método para guardar una imagen en el sistema de archivos.
    public String guardarImagen(MultipartFile imagen) throws IOException {
        if (!imagen.isEmpty()) { // Verifica si el archivo no está vacío
            byte[] bytes = imagen.getBytes(); // Convierte el archivo a un arreglo de bytes
            Path ruta = Paths.get(CARPETA + imagen.getOriginalFilename()); // Define la ruta de guardado
            Files.write(ruta, bytes); // Guarda el archivo en la ruta especificada
            return imagen.getOriginalFilename(); // Retorna el nombre del archivo
        }
        return "default.jpg"; // Retorna un nombre de archivo predeterminado si está vacío
    }

    // Método para eliminar una imagen del sistema de archivos.
    public void eliminarImagen(String nombre) {
        File archivo = new File(CARPETA + nombre); // Define el archivo con su ruta completa
        if (archivo.exists()) { // Verifica si el archivo existe
            archivo.delete(); // Elimina el archivo
        }
    }
}