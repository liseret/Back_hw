#!/usr/bin/env bash


echo -e "1\n"
curl "http://localhost:8080/cards"
echo -e "\n"

echo -e "2\n"
curl "http://localhost:8080/cards?rarity=LEGENDARY"
echo -e "\n"

echo -e "3\n"
curl "http://localhost:8080/cards?name=Pika"
echo -e "\n"

echo -e "4\n"
curl "http://localhost:8080/cards/1"
echo -e "\n"

echo -e "5\n"
curl -i "http://localhost:8080/cards/999"
echo -e "\n"

echo -e "6\n"
curl -i "http://localhost:8080/cards/abc"
echo -e "\n"

echo -e "7\n"
curl -i -X DELETE "http://localhost:8080/cards/5"
echo -e "\n"

echo -e "8\n"
curl -i -X POST "http://localhost:8080/players" \
  -H "Content-Type: application/json" \
  -d '{"name":"che_dum"}'
echo -e "\n"

echo -e "9\n"
curl "http://localhost:8080/players/1"
echo -e "\n"

echo -e "10\n"
curl -i -X POST "http://localhost:8080/players" \
  -H "Content-Type: application/json" \
  -d '{"name":che_dum"}'
echo -e "\n"


echo -e "11\n"
curl -i "$BASE/players/999"
echo -e "\n"

echo -e "12\n"
curl -i -X POST "http://localhost:8080/players" \
  -H "Content-Type: application/json" \
  -d ''
echo -e "\n"

echo -e "13\n"
echo "Выбиваем карточку..."
curl -X POST "http://localhost:8080/players/1/draw"
echo -e "\n"
curl -X POST "http://localhost:8080/players/1/draw"
echo -e "\n"

echo -e "14\n"
curl "http://localhost:8080/players/1/cards"
echo -e "\n"


echo -e "15\n"
curl -i -X PUT "http://localhost:8080/players/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"zaichik_djudi_hops"}'
echo -e "\n"

echo -e "16\n"
curl -i -X DELETE "http://localhost:8080/players/1"
echo -e "\n"

echo -e "18\n"
curl -i "http://localhost:8080/players/1"
echo -e "\n"

echo -e "19\n"
curl -i "http://localhost:8080/unknown"
echo -e "\n"

echo -e "20\n"
curl -i -X PATCH "http://localhost:8080/cards"
echo -e "\n"

echo -e "21\n"
BIG_JSON=$(printf 'a%.0s' {1..100500})
curl -i -X POST "http://localhost:8080/cards" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test\",\"type\":\"NORMAL\",\"rarity\":\"COMMON\",\"art\":\"$BIG_JSON\"}"
echo -e "\n"

