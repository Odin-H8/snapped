package api

import (
	"crypto/rand"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

type User struct {
	Username string
	Password []byte // Hash
	Email    string //todo later...
}

type Token struct {
	Username string
	token    string
	Expiry   time.Time
}

const HASHCOST = 16

var Users []*User
var loggedIn map[*User]Token // Session tokens (used for auth)

func mockInit() {
	p, _ := bcrypt.GenerateFromPassword([]byte("123"), HASHCOST)
	Users = append(Users,
		&User{Username: "Odin", Password: p},
		&User{Username: "Martyna", Password: p},
	)
}

func Login(engine *gin.Engine) {
	mockInit()
	engine.POST("/login", func(ctx *gin.Context) {
		user := User{}
		err := ctx.BindJSON(&user)
		if err != nil {
			ctx.Status(http.StatusBadRequest)
		}

		for _, u := range Users {
			if user.Username == u.Username {
				err := bcrypt.CompareHashAndPassword(u.Password, user.Password)
				if err != nil {
					loggedIn[u] = generateSessionToken(u.Username)
				}
			}
		}
	})
}

func generateSessionToken(username string) Token {
	return Token{
		token:    rand.Text(),
		Username: username,
		Expiry:   time.Now().Add(time.Hour * 12),
	}
}
