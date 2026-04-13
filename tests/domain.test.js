function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

const MASS_UNITS = { g: 1, kg: 1000 };
const VOLUME_UNITS = { ml: 1, l: 1000 };

function detectCategory(unit) {
  if (MASS_UNITS[unit]) return "mass";
  if (VOLUME_UNITS[unit]) return "volume";
  if (unit === "unidad") return "unit";
  return null;
}

function toBaseAmount(quantity, unit, category) {
  if (category === "mass") return quantity * MASS_UNITS[unit];
  if (category === "volume") return quantity * VOLUME_UNITS[unit];
  return quantity;
}

function getBasisSpec(category, unitsInRows) {
  if (category === "mass") {
    if (unitsInRows.has("kg")) return { divisor: 1000, label: "kg" };
    return { divisor: 100, label: "100 g" };
  }

  if (category === "volume") {
    if (unitsInRows.has("l")) return { divisor: 1000, label: "l" };
    return { divisor: 100, label: "100 ml" };
  }

  return { divisor: 1, label: "unidad" };
}

function compareRows(rows) {
  if (!Array.isArray(rows) || rows.length < 2) {
    return { ok: false, error: "Ingresa al menos 2 lineas para comparar." };
  }

  const categories = new Set();
  const unitsInRows = new Set();

  rows.forEach((row) => {
    categories.add(row.category);
    unitsInRows.add(row.unit);
  });

  if (categories.size > 1) {
    return { ok: false, error: "No se pueden mezclar peso, volumen y unidad en la misma comparacion." };
  }

  const category = rows[0].category;
  const basis = getBasisSpec(category, unitsInRows);

  const computed = rows.map((entry) => {
    const amountInBase = toBaseAmount(entry.quantity, entry.unit, entry.category);
    return {
      name: entry.name,
      comparative: (entry.price / amountInBase) * basis.divisor,
    };
  });

  computed.sort((a, b) => a.comparative - b.comparative);

  return {
    ok: true,
    basisLabel: basis.label,
    winner: computed[0],
    ranking: computed,
  };
}

function testComparisonByMass() {
  const rows = [
    { name: "A", price: 3, quantity: 100, unit: "g", category: detectCategory("g") },
    { name: "B", price: 5, quantity: 250, unit: "g", category: detectCategory("g") },
  ];
  const result = compareRows(rows);

  assert(result.ok, "Debe comparar correctamente en masa");
  assert(result.winner.name === "B", "B debe ser la opcion ganadora");
  assert(result.basisLabel === "100 g", "Debe usar base de 100 g cuando no hay kg");
}

function testIncompatibleCategories() {
  const rows = [
    { name: "A", price: 3, quantity: 100, unit: "g", category: detectCategory("g") },
    { name: "B", price: 5, quantity: 3, unit: "unidad", category: detectCategory("unidad") },
  ];
  const result = compareRows(rows);

  assert(!result.ok, "Debe rechazar categorias incompatibles");
}

function testHigherUnitBasis() {
  const rows = [
    { name: "A", price: 3, quantity: 500, unit: "g", category: detectCategory("g") },
    { name: "B", price: 6, quantity: 1, unit: "kg", category: detectCategory("kg") },
  ];
  const result = compareRows(rows);

  assert(result.ok, "Debe calcular comparacion con mezcla de g y kg");
  assert(result.basisLabel === "kg", "Debe usar kg como unidad base al estar presente");
}

export function runDomainTests() {
  testComparisonByMass();
  testIncompatibleCategories();
  testHigherUnitBasis();
  return "3 pruebas ejecutadas correctamente.";
}
