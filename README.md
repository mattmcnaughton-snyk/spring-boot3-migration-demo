# spring-boot3-migration-demo

> **Warning**
> This application is **intentionally insecure**. It exists to demo Snyk scanning and a
> Spring Boot 3 → 4 migration. Never deploy it or expose it on a network.

A small product inventory REST API built on **Spring Boot 3.5** (Java 17, Maven), written
the way a lot of real Boot 3 code is written: it relies on APIs that are deprecated in
Boot 3 and **removed** in Boot 4. Upgrading the parent POM to 4.x will not compile until
the code changes listed below are made.

## Build and run

Needs JDK 17+ and Maven 3.9+.

```bash
mvn clean verify
```

```bash
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. Basic-auth users are `admin` / `Adm1nP@ssw0rd!`
and `user` / `password`.

| Endpoint | Auth | Purpose |
| --- | --- | --- |
| `GET /api/products` | user | List products |
| `GET /api/products/{id}` | user | Get one product |
| `POST /api/products` | user | Create a product |
| `DELETE /api/products/{id}` | user | Soft-delete a product |
| `GET /api/products/search?q=&sort=` | user | Search by name |
| `GET /api/products/report` | user | Async stock report |
| `GET /api/public/greet?name=` | none | HTML greeting |
| `GET /api/public/preview?url=` | none | Fetch a URL preview |
| `GET /api/files/download?name=` | user | Download an uploaded file |
| `GET /api/admin/ping?host=` | admin | Ping a host |

## Spring Boot 4 migration breakers

Each item compiles on Boot 3.5 (most with a deprecation warning, which the build prints
via `-Xlint:deprecation`) and breaks on Boot 4.

| Area | Boot 3 code | What changes in Boot 4 | File |
| --- | --- | --- | --- |
| Embedded server | `spring-boot-starter-undertow` | Undertow support removed; use Tomcat or Jetty | `pom.xml` |
| Starters | `spring-boot-starter-web`, `spring-boot-starter-aop` | Renamed to `spring-boot-starter-webmvc` and `spring-boot-starter-aspectj` | `pom.xml` |
| Spring Security 7 | `csrf().disable()`, `authorizeHttpRequests()`, `.and()`, `httpBasic()`, `headers().frameOptions()` | Non-lambda DSL removed; use `http.csrf(c -> c.disable())` etc. | `config/SecurityConfig.java` |
| Jackson 3 | `com.fasterxml.jackson.databind.*`, `Jackson2ObjectMapperBuilderCustomizer` | Packages move to `tools.jackson.*`; use `JsonMapperBuilderCustomizer` | `config/JacksonConfig.java` |
| Jackson 3 | `@JsonComponent`, `JsonSerializer`, `SerializerProvider` | `@JacksonComponent`, `ValueSerializer`, `SerializationContext` | `json/MoneyJsonComponent.java` |
| Spring Framework 7 | `ListenableFuture`, `AsyncResult` | Removed; return `CompletableFuture` | `service/ReportService.java` |
| Hibernate 7 | `@org.hibernate.annotations.Where` | Removed; use `@SQLRestriction` | `domain/Product.java` |
| Hibernate 7 | `Session.save()`, `Session.update()` | Removed; use `persist()` / `merge()` | `repository/ProductRepositoryCustomImpl.java` |
| Modular auto-config | `org.springframework.boot.web.client.RestTemplateBuilder` | Moves to `org.springframework.boot.restclient` | `config/HttpClientConfig.java` |
| Modular auto-config | `org.springframework.boot.autoconfigure.domain.EntityScan` | Moves to `org.springframework.boot.persistence.autoconfigure` | `InventoryApplication.java` |
| Testing | `@MockBean` | Removed; use `@MockitoBean` | `web/ProductControllerTest.java` |
| Testing | `@WebMvcTest`, `@AutoConfigureMockMvc`, `@DataJpaTest` packages | Move to `org.springframework.boot.webmvc.test.autoconfigure` and `org.springframework.boot.data.jpa.test.autoconfigure` | `src/test/...` |
| Properties | `management.endpoints.enabled-by-default`, `management.endpoint.shutdown.enabled` | Removed; use `management.endpoints.access.default` / `management.endpoint.<id>.access` | `application.properties` |
| Null-safety | `org.springframework.lang.Nullable` | Deprecated in favour of JSpecify `org.jspecify.annotations.Nullable` | `service/ProductService.java` |

## Intentional vulnerabilities

### Snyk Code (SAST)

| Issue | Location |
| --- | --- |
| SQL injection | `ProductService.search`, `ProductService.findByExactName`, `LegacyExportService.exportTable` |
| OS command injection | `AdminController.ping` |
| Deserialization of untrusted data | `AdminController.importBackup` |
| Path traversal | `FileController.download`, `FileController.upload` |
| Reflected XSS | `PublicController.greet` |
| Open redirect | `PublicController.redirect` |
| CSRF protection disabled | `SecurityConfig` |
| Insecure hash (MD5) | `AdminController.fingerprint` |
| Cookie without Secure / HttpOnly | `PublicController.session` |
| SSRF, Text4Shell sink, log injection, weak random, hardcoded credentials, plain-text passwords | `PublicController`, `AdminController`, `AuditAspect`, `SecurityConfig`, `LegacyExportService` |

### Snyk Open Source (SCA)

Pinned on purpose in `pom.xml`:

| Package | Notable CVE |
| --- | --- |
| `org.apache.logging.log4j:log4j-core:2.14.1` | CVE-2021-44228 (Log4Shell) |
| `org.apache.commons:commons-text:1.9` | CVE-2022-42889 (Text4Shell) |
| `commons-collections:commons-collections:3.2.1` | CVE-2015-7501 (deserialization) |
| `commons-fileupload:commons-fileupload:1.4` | CVE-2023-24998 |
| `com.h2database:h2:2.1.210` | CVE-2022-45868 |

Spring Boot 3.5 is past its open-source support window, so the Spring, Jackson, Undertow
and Logback versions it manages also carry known issues. Upgrading to Boot 4 fixes many
of those, which makes this repo useful for showing the security value of the migration.

### Configuration

- Every actuator endpoint (including `env`, `heapdump`, `shutdown`) is exposed without auth.
- The H2 console is enabled and reachable from other hosts.
- Clickjacking protection (`X-Frame-Options`) is turned off.

## Scanning with Snyk

```bash
snyk test
```

```bash
snyk code test
```

Last run (CLI, 2026-10-20): 59 open-source issues, 15 Snyk Code issues.
