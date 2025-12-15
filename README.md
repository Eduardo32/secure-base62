# SecureBase62

SecureBase62 é uma biblioteca Java para codificação e decodificação Base62 com uma chave secreta personalizada. A biblioteca é ideal para criar identificadores curtos, seguros e URL-friendly a partir de strings ou números.

## Características

- **Codificação Base62**: Converte strings e números em representações alfanuméricas compactas
- **Chave secreta personalizada**: Cada instância usa uma chave secreta para embaralhar o alfabeto Base62
- **Suporte para múltiplos tipos**: Codifica/decodifica Strings, Integers e Longs
- **Configuração flexível**: Carrega a chave secreta de arquivos de propriedades ou propriedades do sistema
- **Segurança aprimorada**: Sem chaves padrão - exige configuração explícita
- **Compatível com URLs**: Ideal para encurtar URLs ou criar identificadores amigáveis

## Instalação

### Maven

```xml
<dependency>
    <groupId>com.pauloeduardocosta</groupId>
    <artifactId>securebase62</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.pauloeduardocosta:securebase62:1.0.0'
```

## Configuração

A biblioteca requer uma chave secreta para funcionar. Você pode configurá-la de várias maneiras:

### 1. Configuração via arquivo de propriedades

Crie um arquivo `application.properties` (ou qualquer outro nome) no classpath:

```properties
securebase62.secret.key=sua_chave_secreta_aqui
```

Então carregue a configuração:

```java
SecureBase62 encoder = new SecureBase62("application.properties", true);
```

### 2. Configuração via propriedade do sistema

```java
// Definir a propriedade do sistema

System.setProperty("securebase62.secret.key", "sua_chave_secreta_aqui");

// Criar uma instância que usa a propriedade do sistema
SecureBase62 encoder = new SecureBase62();
```

Ou via linha de comando:

```shell
java -Dsecurebase62.secret.key=sua_chave_secreta_aqui -jar seu-app.jar
```

### 3. Configuração explícita no código

```java
SecureBase62 encoder = new SecureBase62("sua_chave_secreta_aqui");
```

## Uso Básico

### Codificação e Decodificação de Strings

```java
SecureBase62 encoder = new SecureBase62("sua_chave_secreta_aqui");

// Codificar uma string
String original = "Hello World!";
String encoded = encoder.encode(original);
System.out.println("Codificado: " + encoded);

// Decodificar de volta para a string original
String decoded = encoder.decode(encoded);
System.out.println("Decodificado: " + decoded);
```

### Codificação e Decodificação de Números

```java
SecureBase62 encoder = new SecureBase62("sua_chave_secreta_aqui");

// Codificar um Long (ID de banco de dados, por exemplo)
Long databaseId = 12345678901234L;
String encodedId = encoder.encode(databaseId);
System.out.println("ID Codificado: " + encodedId);

// Decodificar de volta para o número original
Long decodedId = encoder.decodeLong(encodedId);
System.out.println("ID Decodificado: " + decodedId);

// Codificar um Integer
Integer simpleId = 987654;
String encodedInt = encoder.encode(simpleId);
System.out.println("Integer Codificado: " + encodedInt);

// Decodificar de volta para o Integer original
Integer decodedInt = encoder.decodeInteger(encodedInt);
System.out.println("Integer Decodificado: " + decodedInt);
```

### Usando a Classe Utilitária

```java
// Certifique-se de que a propriedade do sistema esteja configurada
System.setProperty("securebase62.secret.key", "sua_chave_secreta_aqui");

// Codificar uma string
String encoded = SecureBase62Utils.encode("Hello World!");

// Codificar um Long
String encodedLong = SecureBase62Utils.encode(12345678901234L);

// Codificar um Integer
String encodedInt = SecureBase62Utils.encode(987654);

// Decodificar
String decodedString = SecureBase62Utils.decode(encoded);
Long decodedLong = SecureBase62Utils.decodeLong(encodedLong);
Integer decodedInt = SecureBase62Utils.decodeInteger(encodedInt);
```

## Casos de Uso Comuns

### Encurtador de URLs

```java
@Service
public class UrlShortenerService {

    private final SecureBase62 secureBase62;
    private final UrlRepository urlRepository;
    
    @Autowired
    public UrlShortenerService(SecureBase62 secureBase62, UrlRepository urlRepository) {
        this.secureBase62 = secureBase62;
        this.urlRepository = urlRepository;
    }
    
    public String shortenUrl(String longUrl) {
        // Salvar URL no banco de dados
        UrlEntity entity = new UrlEntity();
        entity.setLongUrl(longUrl);
        entity.setCreatedAt(new Date());
        UrlEntity saved = urlRepository.save(entity);
        
        // Codificar o ID do banco de dados para obter um código curto
        return secureBase62.encode(saved.getId());
    }
    
    public String expandUrl(String shortCode) {
        // Decodificar o código curto para obter o ID do banco de dados
        Long id = secureBase62.decodeLong(shortCode);
        
        // Buscar a URL no banco de dados
        Optional<UrlEntity> entity = urlRepository.findById(id);
        if (entity.isPresent()) {
            return entity.get().getLongUrl();
        } else {
            throw new NotFoundException("URL não encontrada");
        }
    }
}
```

### IDs Amigáveis em APIs RESTful

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final SecureBase62 secureBase62;
    
    @Autowired
    public ProductController(ProductService productService, SecureBase62 secureBase62) {
        this.productService = productService;
        this.secureBase62 = secureBase62;
    }
    
    @GetMapping("/{encodedId}")
    public ResponseEntity<Product> getProduct(@PathVariable String encodedId) {
        try {
            // Decodificar o ID amigável para o ID real do banco de dados
            Long productId = secureBase62.decodeLong(encodedId);
            
            // Buscar o produto usando o ID real
            Product product = productService.findById(productId);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productService.findAll();
        
        // Converter produtos para DTOs com IDs codificados
        return products.stream()
            .map(p -> new ProductDTO(
                secureBase62.encode(p.getId()), // Codificar o ID para representação amigável
                p.getName(),
                p.getPrice()
            ))
            .collect(Collectors.toList());
    }
}
```

### Integração com Spring Boot

```java
@Configuration
public class SecureBase62Config {

    @Value("${securebase62.secret.key}")
    private String secretKey;
    
    @Bean
    public SecureBase62 secureBase62() {
        return new SecureBase62(secretKey);
    }
}
```

## Gerando uma Chave Secreta

Use o utilitário para gerar uma chave secreta forte:

```java
String randomKey = SecureBase62Utils.generateRandomKey(32);
System.out.println("Chave secreta gerada: " + randomKey);
```

## Considerações de Segurança

- **Não compartilhe sua chave secreta**: Mantenha-a segura como qualquer outra credencial
- **Use variáveis de ambiente**: Em produção, considere injetar a chave secreta via variáveis de ambiente
- **Chaves diferentes para ambientes diferentes**: Use chaves diferentes para desenvolvimento, teste e produção
- **Rotação de chaves**: Considere mudar sua chave periodicamente para segurança adicional

## Limitações

- A decodificação requer a mesma chave secreta usada para codificação
- Mudar a chave secreta invalidará todos os IDs codificados anteriormente
- A biblioteca não é compatível com implementações Base62 padrão devido ao embaralhamento do alfabeto

## Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.
