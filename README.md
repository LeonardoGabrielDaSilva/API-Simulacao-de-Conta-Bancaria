# API Conta Bancária

### Endpoints
-----
* Endpoint para criar uma conta nova

*localhost:8090/conta/criar*

numAgencia

Correntista{nome,cpf,nascimento}

limite

saldoInicial

-----
* Endpoint para consultar o saldo de uma conta

*localhost:8090/conta/consultarSaldo*

numAgencia

numConta

-----
* Endpoint para depositar saldo em uma conta

*localhost:8090/conta/depositar*

numAgencia

numConta

valor

-----
* Endpoint para sacar saldo de uma conta

*localhost:8090/conta/sacar*

numAgencia

numConta

valor

------
* Endpoint para realizar uma transferência entre contas

*localhost:8090/conta/transferir*

numAgenciaOrig

numContaOrig

numAgenciaDest

numContaDest

valor

------
* Endpoint para consultar os dados e contas de um correntista

*localhost:8090/conta/consultarCorrentista*

cpf

-----
* Endpoint para desativar uma conta

*localhost:8090/conta/desativar*

numAgencia

numConta
