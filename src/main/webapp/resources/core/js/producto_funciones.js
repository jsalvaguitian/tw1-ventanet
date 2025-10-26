export function validarCamposObligatoriosDeProducto(inputNombreValue, inputPrecioValue,inputStockValue, tipoProductoIdValue, marcaIdValue, 
  presentacionIdValue) {
    return inputNombreValue?.length > 0 && inputPrecioValue > 0 && inputStockValue >= 0 && tipoProductoIdValue > 0
    && marcaIdValue > 0 && presentacionIdValue > 0;
  }

export function validarCamposMayoresACeroDeProducto(inputValMayorACeroValue) {
    return inputValMayorACeroValue > 0;
  }

  export function validarCamposNumericosMayoresACero(value) { 
  const num = Number(value);  
  return !Number.isNaN(num) && num > 0;
}
