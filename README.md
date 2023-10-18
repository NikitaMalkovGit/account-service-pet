# account-service-pet


## Основные этапы работы над проектом:
1. Service structure.  
  Введение функциональных enpoints для реализации сервисных и бизнес процессов.
2. The authentication.
  Реализация процессов аутентификации с имплементацией UserDetailService.
3. Security configuration.
  Введение проверки корректности используемого пароля на процессе регистрации в соответствии с нормами. 
4. Business logic.  
  Добавление возможности вносить изменения в зарплатные ведомости работников.
5. The authorization.    
  Введение ролевой модели и соответствующих endpoints на процессе авторизации.
6. Logging events.    
  Набор logging events для реализации защиты от брут-форс атак. 
7. Securing connection.  
  Введение HTTPS протокола с использованием keystore сертификата.
### Итоговая ролевая модель: 
|                           | Anonymous | User | Accountant | Administrator | Auditor |
|---------------------------|:---------:|:----:|:----------:|:-------------:|:-------:|
| POST api/auth/signup      |     +     |  +   |     +      |       +       |    +    |
| POST api/auth/changepass  |     -     |  +   |     +      |       +       |    -    |
| GET api/empl/payment      |     -     |  +   |     +      |       -       |    -    |
| POST api/acct/payments    |     -     |  -   |     +      |       -       |    -    |
| PUT api/acct/payments     |     -     |  -   |     +      |       -       |    -    |
| GET api/admin/user        |     -     |  -   |     -      |       +       |    -    |
| DELETE api/admin/user     |     -     |  -   |     -      |       +       |    -    |
| PUT api/admin/user/role   |     -     |  -   |     -      |       +       |    -    |
| PUT api/admin/user/access |     -     |  -   |     -      |       +       |    -    |
| GET api/security/events   |     -     |  -   |     -      |       -       |    +    |
