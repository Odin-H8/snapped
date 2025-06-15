package main

import (
	"log"
	"os"
	"os/signal"
	"snapend/api"
	"syscall"

	"github.com/gin-gonic/gin"
)

func main() {
	engine := gin.Default()
	api.MockInit()
	api.Messages(engine)

	go func() {
		err := engine.Run()
		if err != nil {
			log.Println("Engine shut down unexpectedly!")
		}
	}()

	signals := make(chan os.Signal, 1)
	signal.Notify(signals, syscall.SIGINT, syscall.SIGTERM)
	select {
	case <-signals:
		os.Exit(0)
	}
}
