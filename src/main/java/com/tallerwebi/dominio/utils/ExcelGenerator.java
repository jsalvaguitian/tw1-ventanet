package com.tallerwebi.dominio.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelGenerator {

    public byte[] generarPlantillaProductos() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");

        String[] columnas = {
                "Nombre", "Precio", "Descripcion", "Nombre_Imagen", "Stock",
                "Tipo_producto", "Marca", "Presentacion", "Subtipo_producto", "Ancho", "Alto",
                "Material", "Tipo_vidrio", "Color"
        };
        // Estilo del Encabezado en excel
        CellStyle estiloEncabezado = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        estiloEncabezado.setFont(font);
        estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);

        // fondo encabezado
        estiloEncabezado.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fila de encabezado
        Row filaEncabezado = sheet.createRow(0);

        for (int i = 0; i < columnas.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estiloEncabezado);
            sheet.setColumnWidth(i, 6000); // ancho fijo

        }
        // Filas de ejemplo

        CellStyle estiloCentrado = workbook.createCellStyle();
        estiloCentrado.setAlignment(HorizontalAlignment.CENTER);

        Row filaEjemplo = sheet.createRow(1);
        String[] valores = {
                "Ventana corrediza 1x1", "20000", "Ventana de aluminio color blanco",
                "ventana1.jpg", "15", "Ventana", "Aluar", "metro", "Corrediza",
                "100", "100", "Aluminio", "Simple", "Blanco"
        };

        for (int i = 0; i < valores.length; i++) {
            Cell celda = filaEjemplo.createCell(i);
            celda.setCellValue(valores[i]);
            celda.setCellStyle(estiloCentrado);
        }

        // Escribir archivo
        ByteArrayOutputStream bos = new ByteArrayOutputStream();// flujo de salida en memoria para el excel
        workbook.write(bos);// se escribe en el flujo de salida el excel
        workbook.close();// cierro el archivo excel y lo libero

        return bos.toByteArray();// envio mi archivo excel virtual para el proveedor

    }

}
