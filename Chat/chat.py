import json, os

from sanic import Sanic, response
from sanic.log import logger
from sanic.websocket import WebSocketProtocol
from websockets import ConnectionClosed


MESSAGE_CONFIRMATION = os.getenv("MESSAGE_CONFIRMATION", False)

app = Sanic(__name__)

sockets = []


class Message:
    def __init__(self, uname, message):
        self.message = message
        self.username = uname

    def to_json(self):
        return {"username": self.username, "text": self.message}


@app.websocket("/")
async def chat(request, ws):
    """Begins a "chat" websocket with a client.

    Args:
        request: The incomming http request.
        ws: The websocket connection with the client.
    """
    sockets.append(ws)

    while not ws.closed:
        message = await ws.recv()
        logger.debug({"Message": message})
        for socket in sockets:
            try:
                if socket != ws:
                    await socket.send(message)
                elif MESSAGE_CONFIRMATION:
                    ret_mess = Message(
                        "**Kite Server**", f"message received: {message}"
                    ).to_json()
                    await socket.send(json.dumps(ret_mess))
            except ConnectionClosed:
                sockets.remove(socket)

    sockets.remove(ws)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5500, debug=True, protocol=WebSocketProtocol)
