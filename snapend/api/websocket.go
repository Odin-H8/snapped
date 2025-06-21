package api

import (
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool { return true },
}

var clients = make(map[*websocket.Conn]bool)
var broadcast = make(chan Message)

func handleWebsocket(c *gin.Context) {
	ws, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("websocket upgrade error", err)
		return
	}
	defer ws.Close()
	clients[ws] = true
	for {
		var msg Message
		err := ws.ReadJSON(&msg)
		if err != nil {
			log.Println("websocket read error")
			delete(clients, ws)
			break
		}
		messages = append(messages, msg)
		broadcast <- msg
	}
}

func HandleBroadcast() {
	for {
		msg := <-broadcast
		for client := range clients {
			err := client.WriteJSON(msg)
			if err != nil {
				log.Println("websocket send error", err)
				client.Close()
				delete(clients, client)
			}
		}
	}
}
