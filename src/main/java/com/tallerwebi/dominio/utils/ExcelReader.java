package com.tallerwebi.dominio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.tallerwebi.dominio.excepcion.DatoInvalidoExcelException;
import com.tallerwebi.dominio.excepcion.ExcelReaderException;
import com.tallerwebi.dominio.excepcion.PlantillaInvalidaException;
import com.tallerwebi.presentacion.dto.ProductoImportDTO;

public class ExcelReader {

    // Encabezados esperados en el Excel
    private static final String[] HEADERS_ESPERADOS = {
            "Nombre", "Precio", "Descripcion", "Nombre_Imagen", "Stock",
            "Tipo_producto", "Marca", "Presentacion", "Subtipo_producto", "Ancho", "Alto",
            "Material", "Tipo_vidrio", "Color"
    };

    // Lee los productos desde un archivo Excel (.xlsx)

    public static List<ProductoImportDTO> leerProductos(MultipartFile archivoExcel) throws ExcelReaderException {

        List<ProductoImportDTO> productos = new ArrayList<>();

        try (InputStream iStream = archivoExcel.getInputStream();
                Workbook workbook = new XSSFWorkbook(iStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            if (!iterator.hasNext()) {
                throw new PlantillaInvalidaException("El archivo está vacío");
            }

            // Validar encabezado
            Row headerRow = iterator.next();
            validarEncabezado(headerRow);

            // Procesar filas de datos
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (esFilaVacia(row))
                    continue;

                ProductoImportDTO dto = new ProductoImportDTO();
                dto.setNombre(getStringValue(row.getCell(0)));
                dto.setPrecio(getDoubleValue(row.getCell(1)));
                dto.setDescripcion(getStringValue(row.getCell(2)));
                dto.setNombreImagen(getStringValue(row.getCell(3)));
                dto.setStock(getIntegerValue(row.getCell(4)));
                dto.setTipoProducto(getStringValue(row.getCell(5)));
                dto.setMarca(getStringValue(row.getCell(6)));
                dto.setPresentacion(getStringValue(row.getCell(7)));
                dto.setSubtipoProducto(getStringValue(row.getCell(8)));
                dto.setAncho(getStringValue(row.getCell(9)));
                dto.setAlto(getStringValue(row.getCell(10)));
                dto.setMaterial(getStringValue(row.getCell(11)));
                dto.setTipoVidrio(getStringValue(row.getCell(12)));
                dto.setColor(getStringValue(row.getCell(13)));

                productos.add(dto);
            }

            return productos;

        } catch (PlantillaInvalidaException | DatoInvalidoExcelException e) {
            throw e;
        } catch (IOException e) {
            throw new ExcelReaderException("Error al leer el archivo Excel", e);
        }
    }

    // ------------------------------------------------------------------------

    private static double getDoubleValue(Cell cell) throws DatoInvalidoExcelException {
        if (cell == null) {
            throw new DatoInvalidoExcelException("Celda nula o vacía");
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String valor = cell.getStringCellValue().trim();
                if (valor.isEmpty())
                    throw new DatoInvalidoExcelException("Celda vacía");
                return Double.parseDouble(valor);
            } else {
                throw new DatoInvalidoExcelException("El valor no es numérico");
            }
        } catch (NumberFormatException e) {
            throw new DatoInvalidoExcelException("No es un número válido");
        }
    }

    private static Integer getIntegerValue(Cell cell) throws DatoInvalidoExcelException {
        return (int) getDoubleValue(cell);
    }

    private static String getStringValue(Cell cell) throws DatoInvalidoExcelException {
        if (cell == null || cell.getCellType() == CellType.BLANK ||
                (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty())) {
            throw new DatoInvalidoExcelException("Celda vacía");
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return "";
        }
    }

    private static boolean esFilaVacia(Row row) {
        if (row == null)
            return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void validarEncabezado(Row headerRow) throws PlantillaInvalidaException {
        if (headerRow == null) {
            throw new PlantillaInvalidaException("La hoja no contiene encabezados.");
        }

        for (int i = 0; i < HEADERS_ESPERADOS.length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) {
                throw new PlantillaInvalidaException("Falta columna esperada: " + HEADERS_ESPERADOS[i]);
            }

            String valor = cell.getStringCellValue().trim();
            if (!valor.equalsIgnoreCase(HEADERS_ESPERADOS[i])) {
                throw new PlantillaInvalidaException(
                        "Encabezado inválido en columna " + (i + 1) +
                                ". Esperado: '" + HEADERS_ESPERADOS[i] +
                                "', encontrado: '" + valor + "'");
            }
        }
    }

}
