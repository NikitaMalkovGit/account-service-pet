# Account service

## Описание проекта:  
  Релаизация финального проекта из специализации "Spring Security for Backend Developers" на учебнной платформе JetBrains.   
  Компании отправляют выплаты сотрудникам с использованием корпоративной почты. Однако это решение имеет  
  определенные недостатки, связанные с безопасностью и удобством. В рамках данного проекта, была  
  предложена идея отправки выплат на счет сотрудника через корпоративный веб-сайт, основанный на Java и Spring Framework.  
  Была произведена разработка структуры API, определена ролевая модель, внедрена бизнес-логика и обеспечена безопасность сервиса.  

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

### Список logging events
|Описание                       | Событие   | 
|:--------:                     |:--------: |
|Пользователь успешно зарегистрирован|```CREATE_USER```|
|Пользователь успешно изменил пароль|```CHANGE_PASSWORD```|      
|Пользователь пытается получить доступ к ресурсу без прав доступа|```ACCESS_DENIED```|  
|Неудачная аутентификация|```LOGIN_FAILED```|
|Роль предоставляется пользователю|```GRANT_ROLE```| 
|Роль была отозвана|```REMOVE_ROLE```| 
|Администратор заблокировал пользователя|```LOCK_USER```| 
|Администратор разблокировал пользователя|```UNLOCK_USER```| 
|Администратор удалил пользователя|```DELETE_USER```| 
|Пользователь был заблокирован по подозрению в атаке методом перебора|```BRUTE_FORCE```| 

### Требования:
- Java 11+
- IntelliJ IDEA / Netbeans / Eclipse
