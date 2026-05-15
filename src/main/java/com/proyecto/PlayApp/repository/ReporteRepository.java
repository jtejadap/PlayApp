package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Resena;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReporteRepository {
    private static final String PEDIDOS = "pedidos";
    private static final String PRODUCTOS = "productos";
    private static final String ENTIDADES = "entidades";
    private final MongoTemplate mongoTemplate;

    public long contarPedidos(String restauranteId) {
        if (restauranteId == null) {
            return mongoTemplate.getCollection(PEDIDOS).countDocuments();
        }
        return mongoTemplate.getCollection(PEDIDOS)
                .countDocuments(new Document("restaurantes", restauranteId));
    }

    public long contarProductos(String restauranteId) {
        Query query = new Query();
        addRestaurantProductCriteria(query, restauranteId);
        return mongoTemplate.count(query, PRODUCTOS);
    }

    public long contarRestaurantes() {
        Query query = new Query(Criteria.where("rol").regex("ROLE_ADMIN"));
        return mongoTemplate.count(query, ENTIDADES);
    }

    public List<Document> resumenPedidos(String restauranteId) {
        if (restauranteId == null) {
            return aggregate(PEDIDOS, List.of(
                    new Document("$group", new Document("_id", null)
                            .append("totalPedidos", new Document("$sum", 1))
                            .append("ingresosTotales", new Document("$sum", ifNull("$total", 0)))
                    )
            ));
        }
        return aggregate(PEDIDOS, List.of(
                unwindCarrito(),
                matchItemRestaurante(restauranteId),
                new Document("$group", new Document("_id", null)
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                        .append("ingresosTotales", new Document("$sum", subtotalExpression()))
                ),
                new Document("$project", new Document("_id", 0)
                        .append("totalPedidos", new Document("$size", "$pedidosIds"))
                        .append("ingresosTotales", 1)
                )
        ));
    }

    public List<Document> ingresosMensuales(String restauranteId) {
        if (restauranteId == null) {
            return aggregate(PEDIDOS, List.of(
                new Document("$match", new Document("timestamp", new Document("$ne", null))),
                new Document("$group", new Document("_id", new Document("anio", new Document("$year", "$timestamp"))
                        .append("mes", new Document("$month", "$timestamp")))
                        .append("pedidos", new Document("$sum", 1))
                        .append("ingresos", new Document("$sum", ifNull("$total", 0)))
                ),
                new Document("$sort", new Document("_id.anio", 1).append("_id.mes", 1))
            ));
        }
        return aggregate(PEDIDOS, List.of(
                new Document("$match", new Document("timestamp", new Document("$ne", null))),
                unwindCarrito(),
                matchItemRestaurante(restauranteId),
                new Document("$group", new Document("_id", new Document("anio", new Document("$year", "$timestamp"))
                        .append("mes", new Document("$month", "$timestamp")))
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                        .append("ingresos", new Document("$sum", subtotalExpression()))
                ),
                new Document("$project", new Document("_id", 1)
                        .append("pedidos", new Document("$size", "$pedidosIds"))
                        .append("ingresos", 1)
                ),
                new Document("$sort", new Document("_id.anio", 1).append("_id.mes", 1))
        ));
    }

    public List<Document> pedidosPorEstado(String restauranteId) {
        if (restauranteId == null) {
            return aggregate(PEDIDOS, List.of(
                new Document("$group", new Document("_id", ifNull("$estado", "SIN_ESTADO"))
                        .append("cantidad", new Document("$sum", 1))
                        .append("valor", new Document("$sum", ifNull("$total", 0)))
                ),
                new Document("$sort", new Document("_id", 1))
            ));
        }
        return aggregate(PEDIDOS, List.of(
                unwindCarrito(),
                matchItemRestaurante(restauranteId),
                new Document("$group", new Document("_id", ifNull("$estado", "SIN_ESTADO"))
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                        .append("valor", new Document("$sum", subtotalExpression()))
                ),
                new Document("$project", new Document("_id", 1)
                        .append("cantidad", new Document("$size", "$pedidosIds"))
                        .append("valor", 1)
                ),
                new Document("$sort", new Document("_id", 1))
        ));
    }

    public List<Document> pedidosPorMetodoPago(String restauranteId) {
        if (restauranteId == null) {
            return aggregate(PEDIDOS, List.of(
                new Document("$group", new Document("_id", ifNull("$pago.metodo", "SIN_METODO"))
                        .append("cantidad", new Document("$sum", 1))
                        .append("valor", new Document("$sum", ifNull("$total", 0)))
                ),
                new Document("$sort", new Document("cantidad", -1))
            ));
        }
        return aggregate(PEDIDOS, List.of(
                unwindCarrito(),
                matchItemRestaurante(restauranteId),
                new Document("$group", new Document("_id", ifNull("$pago.metodo", "SIN_METODO"))
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                        .append("valor", new Document("$sum", subtotalExpression()))
                ),
                new Document("$project", new Document("_id", 1)
                        .append("cantidad", new Document("$size", "$pedidosIds"))
                        .append("valor", 1)
                ),
                new Document("$sort", new Document("cantidad", -1))
        ));
    }

    public List<Document> promedioProductosPorPedido(String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(unwindCarrito());
        if (restauranteId != null) {
            pipeline.add(matchItemRestaurante(restauranteId));
        }
        pipeline.addAll(List.of(
                new Document("$group", new Document("_id", "$_id")
                        .append("productos", new Document("$sum", ifNull("$carrito.cantidad", 0)))
                ),
                new Document("$group", new Document("_id", null)
                        .append("promedio", new Document("$avg", "$productos"))
                )
        ));
        return aggregate(PEDIDOS, pipeline);
    }

    public List<Document> productosMasVendidos(int limite, String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(unwindCarrito());
        if (restauranteId != null) {
            pipeline.add(matchItemRestaurante(restauranteId));
        }
        pipeline.addAll(List.of(
                new Document("$group", new Document("_id", new Document("productoId", productIdExpression())
                        .append("nombre", ifNull("$carrito.producto.nombre", "Sin producto"))
                        .append("categoria", "$carrito.producto.categoria"))
                        .append("cantidadVendida", new Document("$sum", ifNull("$carrito.cantidad", 0)))
                        .append("ingresos", new Document("$sum", subtotalExpression()))
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                ),
                new Document("$project", new Document("_id", 1)
                        .append("cantidadVendida", 1)
                        .append("ingresos", 1)
                        .append("pedidos", new Document("$size", "$pedidosIds"))
                ),
                new Document("$sort", new Document("cantidadVendida", -1).append("ingresos", -1)),
                new Document("$limit", limite)
        ));
        return aggregate(PEDIDOS, pipeline);
    }

    public List<Document> valorTotalInventario(String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        if (restauranteId != null) {
            pipeline.add(matchProductRestaurante(restauranteId));
        }
        pipeline.add(
                new Document("$group", new Document("_id", null)
                        .append("valorTotalInventario", new Document("$sum", inventarioExpression()))
                )
        );
        return aggregate(PRODUCTOS, pipeline);
    }

    public List<Document> productosConMasStock(int limite, String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        if (restauranteId != null) {
            pipeline.add(matchProductRestaurante(restauranteId));
        }
        pipeline.addAll(List.of(
                new Document("$project", new Document("nombre", 1)
                        .append("categoria", 1)
                        .append("stock", ifNull("$stock", 0))
                        .append("precio", ifNull("$precio", 0))
                        .append("valorInventario", inventarioExpression())
                ),
                new Document("$sort", new Document("stock", -1).append("nombre", 1)),
                new Document("$limit", limite)
        ));
        return aggregate(PRODUCTOS, pipeline);
    }

    public List<Document> productosPorCategoria(String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        if (restauranteId != null) {
            pipeline.add(matchProductRestaurante(restauranteId));
        }
        pipeline.addAll(List.of(
                new Document("$group", new Document("_id", ifNull("$categoria", 0))
                        .append("productos", new Document("$sum", 1))
                        .append("stock", new Document("$sum", ifNull("$stock", 0)))
                        .append("valorInventario", new Document("$sum", inventarioExpression()))
                ),
                new Document("$sort", new Document("_id", 1))
        ));
        return aggregate(PRODUCTOS, pipeline);
    }

    public List<Document> calificacionPromedio() {
        return aggregate(mongoTemplate.getCollectionName(Resena.class), List.of(
                new Document("$group", new Document("_id", null)
                        .append("promedio", new Document("$avg", "$valoracion"))
                )
        ));
    }

    public List<Document> restaurantesConMasPedidos(int limite) {
        return aggregate(PEDIDOS, List.of(
                new Document("$unwind", new Document("path", "$restaurantes").append("preserveNullAndEmptyArrays", false)),
                new Document("$group", new Document("_id", "$restaurantes")
                        .append("pedidos", new Document("$sum", 1))
                ),
                lookupRestaurante(),
                new Document("$unwind", new Document("path", "$restaurante").append("preserveNullAndEmptyArrays", true)),
                new Document("$project", new Document("restauranteId", "$_id")
                        .append("nombre", ifNull("$restaurante.nombreCompleto", "$_id"))
                        .append("pedidos", 1)
                        .append("productosVendidos", new Document("$literal", 0))
                        .append("ingresos", new Document("$literal", 0))
                ),
                new Document("$sort", new Document("pedidos", -1)),
                new Document("$limit", limite)
        ));
    }

    public List<Document> ventasPorRestaurante(int limite, String restauranteId) {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(unwindCarrito());
        if (restauranteId != null) {
            pipeline.add(matchItemRestaurante(restauranteId));
        }
        pipeline.addAll(List.of(
                new Document("$group", new Document("_id", restaurantIdFromProductExpression())
                        .append("productosVendidos", new Document("$sum", ifNull("$carrito.cantidad", 0)))
                        .append("ingresos", new Document("$sum", subtotalExpression()))
                        .append("pedidosIds", new Document("$addToSet", "$_id"))
                ),
                new Document("$project", new Document("productosVendidos", 1)
                        .append("ingresos", 1)
                        .append("pedidos", new Document("$size", "$pedidosIds"))
                ),
                lookupRestaurante(),
                new Document("$unwind", new Document("path", "$restaurante").append("preserveNullAndEmptyArrays", true)),
                new Document("$project", new Document("restauranteId", "$_id")
                        .append("nombre", ifNull("$restaurante.nombreCompleto", "$_id"))
                        .append("pedidos", 1)
                        .append("productosVendidos", 1)
                        .append("ingresos", 1)
                ),
                new Document("$sort", new Document("ingresos", -1).append("productosVendidos", -1)),
                new Document("$limit", limite)
        ));
        return aggregate(PEDIDOS, pipeline);
    }

    private List<Document> aggregate(String collection, List<Document> pipeline) {
        return mongoTemplate.getCollection(collection).aggregate(pipeline).into(new ArrayList<>());
    }

    private void addRestaurantProductCriteria(Query query, String restauranteId) {
        if (restauranteId != null) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("entidad._id").is(restauranteId),
                    Criteria.where("entidad.id").is(restauranteId)
            ));
        }
    }

    private Document unwindCarrito() {
        return new Document("$unwind", new Document("path", "$carrito").append("preserveNullAndEmptyArrays", false));
    }

    private Document matchItemRestaurante(String restauranteId) {
        return new Document("$match", new Document("$or", List.of(
                new Document("carrito.producto.entidad._id", restauranteId),
                new Document("carrito.producto.entidad.id", restauranteId)
        )));
    }

    private Document matchProductRestaurante(String restauranteId) {
        return new Document("$match", new Document("$or", List.of(
                new Document("entidad._id", restauranteId),
                new Document("entidad.id", restauranteId)
        )));
    }

    private Document lookupRestaurante() {
        return new Document("$lookup", new Document("from", ENTIDADES)
                .append("localField", "_id")
                .append("foreignField", "_id")
                .append("as", "restaurante"));
    }

    private Document productIdExpression() {
        return ifNull("$carrito.producto._id", ifNull("$carrito.producto.id", "$carrito.producto.nombre"));
    }

    private Document restaurantIdFromProductExpression() {
        return ifNull("$carrito.producto.entidad._id", ifNull("$carrito.producto.entidad.id", "$carrito.producto.entidad.correo"));
    }

    private Document inventarioExpression() {
        return new Document("$multiply", List.of(ifNull("$precio", 0), ifNull("$stock", 0)));
    }

    private Document subtotalExpression() {
        return ifNull("$carrito.subtotal",
                new Document("$multiply", List.of(ifNull("$carrito.producto.precio", 0), ifNull("$carrito.cantidad", 0))));
    }

    private Document ifNull(Object value, Object fallback) {
        return new Document("$ifNull", List.of(value, fallback));
    }
}
