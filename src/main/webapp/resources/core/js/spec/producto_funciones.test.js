import { validarCamposObligatoriosDeProducto } from "../producto_funciones.js";

describe("Producto Funciones", function() {
  it("debe devolver true cuando los campos nombre, precio, stock, tipo de Producto, marca, proveedor y presentacion son validos", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 10000, 5, 1, 1,1)).toBe(true);
  });

  it("debe devolver false cuando los campos precio, stock, tipo de Producto, marca, proveedor y presentacion son validos pero nombre es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("", 10000, 5, 1, 1,1)).toBe(false);
  });

  it("debe devolver false cuando los campos nombre, stock, tipo de Producto, marca, proveedor y presentacion son validos pero precio es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 0, 5, 1, 1)).toBe(false);
  });

  it("debe devolver false cuando los campos nombre,precio, tipo de Producto, marca, proveedor y presentacion son validos pero stock es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 10000, -1, 1, 1)).toBe(false);
  });
  
  it("debe devolver false cuando los campos nombre,precio, stock, marca, proveedor y presentacion son validos pero tipo de Producto es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 10000, 1, 0, 1,1)).toBe(false);
  });

  it("debe devolver false cuando los campos nombre,precio, stock, tipo de Producto, proveedor y presentacion son validos pero marca es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 10000, 5, 1, 0,1)).toBe(false);
  });
  
  // it("debe devolver false cuando los campos nombre,precio, stock, tipo de Producto, marca y presentacion son validos pero proveedor es invalido", function() {
  //   expect(validarCamposObligatoriosDeProducto("producto1", 10000, 5, 1, 1,1)).toBe(false);
  // });

  it("debe devolver false cuando los campos nombre,precio, stock, tipo de Producto, marca y proveedor son validos pero presentacion es invalido", function() {
    expect(validarCamposObligatoriosDeProducto("producto1", 10000, 5, 1, 1,0)).toBe(false);
  });   
});