# theseus

## Supported requests

### classification

[GET]       /classification/_all 					            : liefert alle IDs
[GET]       /classification?code={c1}&code={c2}&…     : liefert Classification-Objekte für angegebene IDs
[POST]      /classification {requestBody} 			      : schreibt das geänderte Objekt, return value: Status?
[PUT]       /classification {requestBody} 			      : erstellt ein neues Objekt, return value: ID des neu erstellten Objektes
[DELETE]    /classification?code={c}				          : löscht ein Objekt, return value: Status?
[GET]       /classification/_filter?type={t}			    : liefert IDs von Objekten, die vom Typ {t} sind

### classificationType

[GET]       /classificationType/_all 					        : liefert alle IDs
[GET]       /classificationType?code={c1}&code={c2}&… : liefert ClassificationType-Objekte für angegebene IDs
[POST]      /classificationType {requestBody} 			  : schreibt das geänderte Objekt, return value: Status?
[PUT]       /classificationType {requestBody} 			  : erstellt ein neues Objekt, return value: ID des neu erstellten Objektes
[DELETE]    /classificationType?code={c}				      : löscht ein Objekt, return value: Status?
