(() => {
    const MAX_MESSAGE_LENGTH = 1000;
    const SESSION_STORAGE_KEY = "playapp_chat_session_id";

    const root = document.querySelector(".playapp-chat-root");
    if (!root) {
        return;
    }

    const toggleButton = document.getElementById("playapp-chat-toggle");
    const closeButton = document.getElementById("playapp-chat-close");
    const windowElement = document.getElementById("playapp-chat-window");
    const form = document.getElementById("playapp-chat-form");
    const input = document.getElementById("playapp-chat-input");
    const sendButton = document.getElementById("playapp-chat-send");
    const messagesContainer = document.getElementById("playapp-chat-messages");
    const typingIndicator = document.getElementById("playapp-chat-typing");
    const errorElement = document.getElementById("playapp-chat-error");

    let sessionId = localStorage.getItem(SESSION_STORAGE_KEY);
    let loadedHistoryForSession = null;

    const sanitizeInput = (value) => value
        .replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F\u007F]/g, "")
        .replace(/[<>]/g, "")
        .trim();

    const toggleChat = (isOpen) => {
        root.classList.toggle("playapp-chat-open", isOpen);
        toggleButton.setAttribute("aria-expanded", String(isOpen));
        windowElement.setAttribute("aria-hidden", String(!isOpen));
        if (isOpen) {
            input.focus();
            scrollToBottom();
            loadHistory();
        }
    };

    const scrollToBottom = () => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    };

    const formatTime = (value) => {
        if (!value) {
            return "";
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return "";
        }
        return date.toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" });
    };

    const appendMessage = (role, content, timestamp) => {
        const bubble = document.createElement("article");
        const normalizedRole = role === "user" ? "user" : (role === "assistant" ? "assistant" : "system");
        bubble.className = `playapp-chat-bubble playapp-chat-bubble-${normalizedRole}`;
        bubble.textContent = content;

        const time = formatTime(timestamp);
        if (time) {
            const meta = document.createElement("small");
            meta.className = "playapp-chat-meta";
            meta.textContent = time;
            bubble.appendChild(meta);
        }

        messagesContainer.appendChild(bubble);
        scrollToBottom();
    };

    const showTyping = (visible) => {
        typingIndicator.hidden = !visible;
        if (visible) {
            scrollToBottom();
        }
    };

    const showError = (visible, text) => {
        if (text) {
            errorElement.textContent = text;
        }
        errorElement.hidden = !visible;
    };

    const loadHistory = async () => {
        if (!sessionId || loadedHistoryForSession === sessionId) {
            return;
        }

        try {
            showError(false);
            const response = await fetch(`/api/chat/history/${encodeURIComponent(sessionId)}`);
            if (!response.ok) {
                throw new Error("history-error");
            }

            const payload = await response.json();
            messagesContainer.innerHTML = "";
            const items = Array.isArray(payload.messages) ? payload.messages : [];
            items.forEach((message) => {
                appendMessage(message.role, message.content, message.timestamp);
            });
            loadedHistoryForSession = sessionId;
        } catch (error) {
            localStorage.removeItem(SESSION_STORAGE_KEY);
            sessionId = null;
            loadedHistoryForSession = null;
            messagesContainer.innerHTML = "";
            appendMessage("assistant", "Iniciamos un chat nuevo para continuar.");
        }
    };

    const sendMessage = async () => {
        const rawMessage = input.value || "";
        const message = sanitizeInput(rawMessage);

        if (!message) {
            showError(true, "Escribe un mensaje para continuar.");
            return;
        }

        if (message.length > MAX_MESSAGE_LENGTH) {
            showError(true, `El mensaje supera ${MAX_MESSAGE_LENGTH} caracteres.`);
            return;
        }

        input.value = "";
        input.style.height = "auto";
        showError(false);
        appendMessage("user", message, new Date().toISOString());

        sendButton.disabled = true;
        showTyping(true);

        try {
            const response = await fetch("/api/chat/send", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    sessionId,
                    message
                })
            });

            const payload = await response.json();
            if (!response.ok) {
                throw new Error(payload.error || "send-error");
            }

            sessionId = payload.sessionId || sessionId;
            if (sessionId) {
                localStorage.setItem(SESSION_STORAGE_KEY, sessionId);
            }
            appendMessage("assistant", payload.reply || "Ahora no pude responder, intenta de nuevo.", payload.timestamp);
        } catch (error) {
            showError(true, "No fue posible responder. Intenta de nuevo.");
            appendMessage("assistant", "No fue posible responder. Intenta de nuevo.");
        } finally {
            showTyping(false);
            sendButton.disabled = false;
        }
    };

    toggleButton.addEventListener("click", () => {
        const isOpen = root.classList.contains("playapp-chat-open");
        toggleChat(!isOpen);
    });

    closeButton.addEventListener("click", () => toggleChat(false));

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        await sendMessage();
    });

    input.addEventListener("input", () => {
        input.style.height = "auto";
        input.style.height = `${Math.min(input.scrollHeight, 110)}px`;
        showError(false);
    });

    input.addEventListener("keydown", async (event) => {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            await sendMessage();
        }
    });

    appendMessage("assistant", "Hola, soy el asistente de PlayApp. En que te ayudo?");
})();
