# account-service-pet


## Основные этапы работы над проектом:
1. Service structure.

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
