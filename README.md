# BoardCamp - API de Locadora de Jogos de Tabuleiro

API em **Java** para gerenciar uma locadora de jogos de tabuleiro, utilizando **PostgreSQL** para persistência de dados. O projeto segue arquitetura em camadas: `controllers`, `services`, `repositories`, `models` e `dtos`.

## Funcionalidades

### Jogos
- **Listar jogos:** `GET /games`
- **Adicionar jogo:** `POST /games`  
  - Validações: `name` único e presente; `stockTotal` e `pricePerDay` > 0.

### Clientes
- **Listar clientes:** `GET /customers`
- **Buscar cliente por id:** `GET /customers/:id`
- **Adicionar cliente:** `POST /customers`  
  - Validações: `cpf` único e 11 caracteres; `phone` 10 ou 11 dígitos; `name` presente.

### Aluguéis
- **Listar aluguéis:** `GET /rentals`
- **Registrar aluguel:** `POST /rentals`  
  - Calcula `rentDate` e `originalPrice` automaticamente.  
  - Verifica disponibilidade do jogo.
- **Finalizar aluguel:** `POST /rentals/:id/return`  
  - Calcula `delayFee` baseado em dias de atraso.
- **Apagar aluguel:** `DELETE /rentals/:id`  
  - Só é possível apagar se já finalizado.

## Tecnologias
- Java 17+
- Spring Boot
- PostgreSQL
- JUnit + Mockito (testes unitários e de integração)

