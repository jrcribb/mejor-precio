# Mejor Precio (Vanilla SPA)

SPA sin frameworks y sin Node para comparar hasta 3 presentaciones de un producto.

## Funcionalidad actual

- Todo el CSS + JS + SVG esta embebido dentro de `mejor-precio.html`.
- Grilla fija de 3 lineas con `precio`, `cantidad`, `peso` y columna comparativa.
- El comparativo se muestra por la unidad mayor usada en la comparacion actual:
  - Masa: `kg` si hay al menos una fila en kg, en caso contrario `100 g`.
  - Volumen: `l` si hay al menos una fila en l, en caso contrario `100 ml`.
  - Unidad: `unidad`.
- El valor comparativo usa simbolo `$`.
- Se marca una sola mejor opcion con fila en color inverso.
- Salida con maximo 2 decimales.
- Recalculo automatico al salir del ultimo campo (`peso`) de una linea.
- Modo claro/oscuro y control de tamano de letra con `+` y `-`.

## Ejecutar

1. Abrir `mejor-precio.html` en un navegador moderno.
2. Ingresar al menos 2 lineas; al salir del campo `peso` se recalcula automaticamente.
