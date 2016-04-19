# theseus

## URLs

http://fourigin.de/spring/prototype/classification/_all

http://fourigin.de/spring/prototype/classification/?code=1yy&code=1yz

http://fourigin.de/spring/prototype/classificationType/_all

http://fourigin.de/spring/prototype/classificationType/?code=model&code=trim


## Supported requests

### classification

| Method   | URL                                   | Description                                                             |
|----------|---------------------------------------|-------------------------------------------------------------------------|
| [GET]    | /classification/_all                  | liefert alle IDs                                                        |
| [GET]    | /classification?code={c1}&code={c2}&… | liefert Classification-Objekte für angegebene IDs                       |
| [POST]   | /classification {requestBody}         | schreibt das geänderte Objekt, return value: Status?                    |
| [PUT]    | /classification {requestBody}         | erstellt ein neues Objekt, return value: ID des neu erstellten Objektes |
| [DELETE] | /classification?code={c}              | löscht ein Objekt, return value: Status?                                |
| [GET]    | /classification/_filter?type={t}      | liefert IDs von Objekten, die vom Typ {t} sind                          |

### classificationType

| Method   | URL                                       | Description                                                             |
|----------|-------------------------------------------|-------------------------------------------------------------------------|
| [GET]    | /classificationType/_all                  | liefert alle IDs                                                        |
| [GET]    | /classificationType?code={c1}&code={c2}&… | liefert ClassificationType-Objekte für angegebene IDs                   |
| [POST]   | /classificationType {requestBody}         | schreibt das geänderte Objekt, return value: Status?                    |
| [PUT]    | /classificationType {requestBody}         | erstellt ein neues Objekt, return value: ID des neu erstellten Objektes |
| [DELETE] | /classificationType?code={c}              | löscht ein Objekt, return value: Status?                                |
