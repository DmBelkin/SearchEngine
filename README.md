Diploma on Skillbox course


�������� ���-����������

���-��������� (frontend-������������) ������� ������������ ����� ���� ���-�������� � ����� ���������:

Dashboard. ��� ������� ����������� �� ���������. �� ��� ������������ ����� ���������� �� ���� ������, � ����� ��������� ���������� � ������ �� ������� �� ������ (����������, ���������� �� ������� /api/statistics).



Management. �� ���� ������� ��������� ����������� ���������� ��������� ������� � ������ � ��������� ������ ���������� (��������������), � ����� ����������� �������� (��������) ��������� �������� �� ������:



Search. ��� �������� ������������� ��� ������������ ���������� ������. �� ��� ��������� ���� ������, ���������� ������ � ������� ����� ��� ������, � ��� ������� �� ������ ������ ��������� ���������� ������ (�� API-������� /api/search):



��� ���������� �� ������� ������������ ���� �������� � API ������ ����������. ��� ������� ������ ����� ������������ �������.

��������� ���� ������

site � ���������� � ������ � �������� �� ����������

id INT NOT NULL AUTO_INCREMENT;
status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL � ������� ������ ������ ���������� �����, ���������� ���������� ���������� ������ ������������ ����� �� ����� � ���������� ��� �������������� � ��������, ���� ��������� ��������������� (����� � ������) ���� ��� �� ������� ���������������� (���� �� ����� � ������ � �� ����� �� ���������� ������ � ����������� ����������);
status_time DATETIME NOT NULL � ���� � ����� ������� (� ������ ������� INDEXING ���� � ����� ������ ����������� ��������� ��� ���������� ������ ����� �������� � ������);
last_error TEXT � ����� ������ ���������� ��� NULL, ���� � �� ����;
url VARCHAR(255) NOT NULL � ����� ������� �������� �����;
name VARCHAR(255) NOT NULL � ��� �����.

page � ������������������ �������� �����

id INT NOT NULL AUTO_INCREMENT;
site_id INT NOT NULL � ID ���-����� �� ������� site;
path TEXT NOT NULL � ����� �������� �� ����� ����� (������ ���������� �� �����, ��������: /news/372189/);
code INT NOT NULL � ��� HTTP-������, ���������� ��� ������� �������� (��������, 200, 404, 500 ��� ������);
content MEDIUMTEXT NOT NULL � ������� �������� (HTML-���).

�� ���� path ������ ���� ���������� ������, ����� ����� �� ���� ��� �������, ����� � ��� ����� ����� ������. ������� ����������� � ����� ����� �������� SQL�.

lemma � �����, ������������� � ������� (��. ���������: ������������).

id INT NOT NULL AUTO_INCREMENT;
site_id INT NOT NULL � ID ���-����� �� ������� site;
lemma VARCHAR(255) NOT NULL � ���������� ����� ����� (�����);
frequency INT NOT NULL � ���������� �������, �� ������� ����� ����������� ���� �� ���� ���. ������������ �������� �� ����� ��������� ����� ���������� ���� �� �����.

index � ��������� ������

id INT NOT NULL AUTO_INCREMENT;
page_id INT NOT NULL � ������������� ��������;
lemma_id INT NOT NULL � ������������� �����;
rank FLOAT NOT NULL � ���������� ������ ����� ��� ������ ��������.

������������ API

������ ������ ���������� � GET /api/startIndexing

����� ��������� ������ ���������� ���� ������ ��� ������ ��������������, ���� ��� ��� ����������������.
���� � ��������� ������ ���������� ��� �������������� ��� ��������, ����� ���������� ��������������� ��������� �� ������.

���������:

����� ��� ����������

������ ������ � ������ ������:

{
'result': true
}

������ ������ � ������ ������:

{
'result': false,
'error': "���������� ��� ��������"
}

��������� ������� ���������� � GET /api/stopIndexing

����� ������������� ������� ������� ���������� (��������������). ���� � ��������� ������ ���������� ��� �������������� �� ����������, ����� ���������� ��������������� ��������� �� ������.

���������:

����� ��� ����������.

������ ������ � ������ ������:

{
'result': true
}

������ ������ � ������ ������:

{
'result': false,
'error': "���������� �� ��������"
}


���������� ��� ���������� ��������� �������� � POST /api/indexPage

����� ��������� � ������ ��� ��������� ��������� ��������, ����� ������� ������� � ���������.
���� ����� �������� ������� �������, ����� ������ ������� ��������������� ������.

���������:

url � ����� ��������, ������� ����� �����������������.

������ ������ � ������ ������:

{
'result': true
}

������ ������ � ������ ������:

{
'result': false,
'error': "������ �������� ��������� �� ��������� ������,
��������� � ���������������� �����"
}


���������� � GET /api/statistics

����� ���������� ���������� � ������ ��������� ���������� � ��������� ��������� �������� � ������ ������.
���� ������ ���������� ���� ��� ����� ����� ���, �������� ���� error �� �����.

���������:

����� ��� ����������.

������ ������:

{
'result': true,
'statistics': {
"total": {
"sites": 10,
"pages": 436423,
"lemmas": 5127891,
"indexing": true
},
"detailed": [
{
"url": "http://www.site.com",
"name": "��� �����",
"status": "INDEXED",
"statusTime": 1600160357,
"error": "������ ����������: �������
�������� ����� ����������",
"pages": 5764,
"lemmas": 321115
},
...
]
}


��������� ������ �� ���������� ������� � GET /api/search

����� ������������ ����� ������� �� ����������� ���������� ������� (�������� query).
����� �������� ���������� ���������, ����� ����� ������ ��������� offset (����� �� ������ ������ �����������) � limit (���������� �����������, ������� ���������� �������).
� ������ ��������� ����� ���������� ����������� (count), �� ��������� �� �������� ���������� offset � limit, � ������ data � ������������ ������. ������ ��������� � ��� ������, ���������� �������� ���������� ������ (��. ���� ��������� � �������� ������� ��������).
���� ��������� ������ �� ����� ��� ��� ��� �������� ������� (����, �� �������� ����, ��� ��� ����� ����� �� ����������������), ����� ������ ������� ��������������� ������ (��. ���� ������). ������ ������ ������ ���� ��������� � �������� ���� ������.

���������:

query � ��������� ������;
site � ����, �� �������� ������������ ����� (���� �� �����, ����� ������ ����������� �� ���� ������������������ ������); ������� � ������� ������, ��������: http://www.site.com (��� ����� � �����);
offset � ����� �� 0 ��� ������������� ������ (�������� ��������������; ���� �� ����������, �� �������� �� ��������� ����� ����);
limit � ���������� �����������, ������� ���������� ������� (�������� ��������������; ���� �� ����������, �� �������� �� ��������� ����� 20).

������ ������ � ������ ������:

{
'result': true,
'count': 574,
'data': [
{
"site": "http://www.site.com",
"siteName": "��� �����",
"uri": "/path/to/page/6784",
"title": "��������� ��������,
������� �������",
"snippet": "�������� ������,
� ������� �������
����������, <b>����������
������</b>, � ������� HTML",
"relevance": 0.93362
},
...
]
}

������ ������ � ������ ������:

{
'result': false,
'error': "����� ������ ��������� ������"
}


������ � ������ ������

�� ���� �������� API ���������� ����������� ���������� ������ � ������ ������������� ������. ����� ����� API ����� ���������� ������, ���� ��� ���������. � ���� ������ ����� ������ ��������� ����������� �������:

{
'result': false,
'error': "��������� �������� �� �������"
}

����� ������ ������ �������������� ���������������� ������-������. ���������� ������������ �������������� ����� 400, 401, 403, 404, 405 � 500 ��� ������������� ��������������� �� ����� ������.

