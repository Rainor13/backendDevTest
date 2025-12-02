# Backend dev technical test
We want to offer a new feature to our customers showing similar products to the one they are currently seeing. To do this we agreed with our front-end applications to create a new REST API operation that will provide them the product detail of the similar products for a given one. [Here](./similarProducts.yaml) is the contract we agreed.

We already have an endpoint that provides the product Ids similar for a given one. We also have another endpoint that returns the product detail by product Id. [Here](./existingApis.yaml) is the documentation of the existing APIs.

**Create a Spring boot application that exposes the agreed REST API on port 5000.**

## Solución implementada

La aplicación sigue arquitectura hexagonal:
- **Dominio**: `domain/model` con la entidad `Product` y puertos (`domain/port`) que definen qué necesita la lógica de negocio.
- **Aplicación**: caso de uso `GetSimilarProductsUseCase` que orquesta la obtención de IDs similares y sus detalles.
- **Adaptadores**:
  - Entrada: controlador REST `ProductController` (`infrastructure/in/web`) que expone `GET /product/{productId}/similar` y mapea excepciones mediante `ApiExceptionHandler`.
  - Salida: clientes HTTP (`infrastructure/out/http`) que consumen los mocks externos usando `RestClient`, con conversión a modelos de dominio.

## Resiliencia y observabilidad

- `RestClient` configurado con *timeouts* (`500ms` conexión, `1500ms` lectura) para cortar peticiones colgadas.
- Resilience4j (`resilience4j-spring-boot3`) añade `@Retry` y `@CircuitBreaker` sobre los adaptadores externos (instancia `productService`) con 3 intentos, ventana de 10 llamadas y umbral de fallo 50%.
- Logging contextual (`WARN/ERROR/INFO`) en los adaptadores para rastrear 404, 5xx y respuestas vacías.
- Actuator habilitado (endpoint `/actuator/health`) y métricas de k6 se almacenan en InfluxDB y se visualizan con Grafana (`shared/grafana` contiene el dashboard).

## Ejecutar la aplicación

```bash
# Instalar dependencias y ejecutar tests
./mvnw test

# Levantar mocks y observabilidad (desde la raíz del repo)
docker-compose up -d simulado influxdb grafana

# Arrancar la aplicación
./mvnw spring-boot:run

# Probar la API
curl http://localhost:5000/product/1/similar
```

## Pruebas automáticas

```bash
# Suite completa (unitaria + integración)
./mvnw test

# Tests unitarios destacados:
# - GetSimilarProductsUseCaseTest: valida el caso de uso con puertos mockeados.
# - HttpProductDetailClientTest / HttpSimilarProductIdsClientTest: usan MockWebServer
#   para comprobar el mapeo de 200/404/500 y la conversión a excepciones de dominio.
```

![Diagram](./assets/diagram.jpg "Diagram")

Note that _Test_ and _Mocks_ components are given, you must only implement _yourApp_.

## Testing and Self-evaluation
You can run the same test we will put through your application. You just need to have docker installed.

First of all, you may need to enable file sharing for the `shared` folder on your docker dashboard -> settings -> resources -> file sharing.

Then you can start the mocks and other needed infrastructure with the following command.
```
docker-compose up -d simulado influxdb grafana
```
Check that mocks are working with a sample request to [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

To execute the test run:
```
docker-compose run --rm k6 run scripts/test.js
```
La ejecución escribe métricas en InfluxDB. Puedes verlas en Grafana: [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) (el dashboard ya está provisionado en `shared/grafana`).

## Evaluation
The following topics will be considered:
- Code clarity and maintainability
- Performance
- Resilience
