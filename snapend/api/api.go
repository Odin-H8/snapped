package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type Message struct {
	Sender    string `json:"sender"`
	Recipient string `json:"recipient"`
	Msg       string `json:"msg"`
}

var messages []Message

func MockInit() {
	messages = append(messages, Message{Sender: "Odin", Recipient: "Martyna", Msg: "Hi"})
	messages = append(messages, Message{Sender: "Martyna", Recipient: "Odin", Msg: "Hello"})
	messages = append(messages, Message{Sender: "Martyna", Recipient: "Odin", Msg: "poo poo"})
}

func Messages(engine *gin.Engine) {
	engine.GET("/msg", func(c *gin.Context) {
		c.JSON(http.StatusOK, messages)
	})

	engine.POST("/msg", func(c *gin.Context) {
		msg := Message{}
		err := c.BindJSON(&msg)
		if err != nil {
			c.Status(http.StatusBadRequest)
			return
		}
		messages = append(messages, msg)
	})
}
