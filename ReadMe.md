# SD-IHW-2
ИДЗ №2 КПО

Консольный узкофункциональный файловый менеджер

1) Без параметров командной строки
2) На вход идёт корневая папка, внутри которой могут быть файлы и директории
3) Если пользователь задал некорректную директорию, то по умолчанию менеджер выбирает в качестве корня путь "Директория проекта\TaskExample"
4) Уровень вложенности не выше 10
5) В содержимом файлов могут быть запросы вида:
require '<путь к другому файлу от корневого каталога>' (предполагается, что кавычки заданы именно таким символом)
6) Во время работы менеджера строится граф по зависимостям между файлами
7) Между конкретным файлом и вершиной графа биективное соотвествие
8) С помощью класса Граф реализуется поиск циклов и сортировка списка файлов по следующему условию:
если файл А, зависит от файла В, то файл А находится ниже файла В в списке (топологическая сортировка)
9) Программа выводит список смежности построенного графа
10) Программа выводит содержимое файлов в порядке сортировки, в консоль и в выходной файл "output.txt"
11) Если были найдены циклы, выводится соответствующее сообщение вместе с вершиной, с которой возникли проблемы
12) Если запрашивается несуществующий файл, выводится соответствующее сообщение
