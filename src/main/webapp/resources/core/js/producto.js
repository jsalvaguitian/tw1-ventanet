import { validarCamposObligatoriosDeProducto } from "./producto_funciones.js";

const btnCrearNode = document.getElementById("btn-crear");
const btnEditNode = document.getElementById("btn-edit");
const inputNombreNode = document.getElementById("nombre");
const inputPrecioNode = document.getElementById("precio");
const inputStockNode = document.getElementById("stock");
const inputTipoProductoIdNode = document.getElementById("tipo_producto_id");
const inputMarcaIdNode = document.getElementById("marca_id");
const inputDescripcionNode = document.getElementById("descripcion");
const inputPresentacionIdNode = document.getElementById("presentacion_id");
const inputImagenFileNode = document.getElementById("imagenFile");



inputNombreNode.addEventListener("keyup", (event) => {
  const nombreValue = event.target.value;
  const precioValue = inputPrecioNode.value;
  const stockValue = inputStockNode.value;
  const tipoProductoIdValue = inputTipoProductoIdNode.value;
  const marcaIdValue  = inputMarcaIdNode.value;
  //const proveedorIdValue  = inputProveedorIdNode.value;
  const presentacionIdValue = inputPresentacionIdNode.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
  
});

inputPrecioNode.addEventListener("keyup", (event) => {
  const nombreValue = inputNombreNode.value;
  const precioValue = event.target.value;
  const stockValue = inputStockNode.value;
  const tipoProductoIdValue = inputTipoProductoIdNode.value;
  const marcaIdValue = inputMarcaIdNode.value;
  //const proveedorIdValue = inputProveedorIdNode.value;
  const presentacionIdValue = inputPresentacionIdNode.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
});

inputStockNode.addEventListener("keyup", (event) => {
  const nombreValue = inputNombreNode.value;
  const precioValue = inputPrecioNode.value;
  const stockValue = event.target.value;
  const tipoProductoIdValue = inputTipoProductoIdNode.value;
  const marcaIdValue = inputMarcaIdNode.value;
  //const proveedorIdValue = inputProveedorIdNode.value;
  const presentacionIdValue = inputPresentacionIdNode.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
});

inputTipoProductoIdNode.addEventListener("change", (event) => {
  const nombreValue = inputNombreNode.value;
  const precioValue = inputPrecioNode.value;
  const stockValue = inputStockNode.value;
  const tipoProductoIdValue = event.target.value;
  const marcaIdValue = inputMarcaIdNode.value;
  //const proveedorIdValue = inputProveedorIdNode.value;
  const presentacionIdValue = inputPresentacionIdNode.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
});

inputMarcaIdNode.addEventListener("change", (event) => {
  const nombreValue = inputNombreNode.value;
  const precioValue = inputPrecioNode.value;
  const stockValue = inputStockNode.value;
  const tipoProductoIdValue = inputTipoProductoIdNode.value;
  const marcaIdValue = event.target.value;
  //const proveedorIdValue = inputProveedorIdNode.value;
  const presentacionIdValue = inputPresentacionIdNode.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
});

inputPresentacionIdNode.addEventListener("change", (event) => {
  const nombreValue = inputNombreNode.value;
  const precioValue = inputPrecioNode.value;
  const stockValue = inputStockNode.value;
  const tipoProductoIdValue = inputTipoProductoIdNode.value;
  const marcaIdValue = inputMarcaIdNode.value;
  //const proveedorIdValue = inputProveedorIdNode.value;
  const presentacionIdValue = event.target.value;
  if(btnCrearNode !== null){
    btnCrearNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }

  if(btnEditNode !== null){
    btnEditNode.disabled = !validarCamposObligatoriosDeProducto(nombreValue, precioValue, stockValue, tipoProductoIdValue, marcaIdValue, presentacionIdValue);
  }
});