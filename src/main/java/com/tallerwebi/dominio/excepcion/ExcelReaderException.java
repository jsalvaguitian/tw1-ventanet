package com.tallerwebi.dominio.excepcion;

import java.io.IOException;

public class ExcelReaderException extends IOException {

    public ExcelReaderException(String message) {
        super(message);
    }

    public ExcelReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
