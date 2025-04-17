<template>
  <div class="chat-container">
    <div class="messages" ref="messagesContainer">
      <ChatMessage
        v-for="(message, index) in messages"
        :key="index"
        :is-user="message.isUser"
        :text="message.text"
      />
    </div>
    <div class="input-container">
      <div class="controls">
        <label class="switch">
          <input type="checkbox" v-model="useTypewriter">
          <span class="slider"></span>
        </label>
        <span class="switch-label">流式响应</span>
      </div>
      <div class="input-group">
        <textarea
          v-model="newMessage"
          @keydown.enter.prevent="sendMessage"
          placeholder="输入消息..."
          rows="1"
          class="message-input"
        />
        <button @click="sendMessage" class="send-button" :disabled="!newMessage.trim()">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import ChatMessage from './ChatMessage.vue';
import { fetchEventSource } from '@microsoft/fetch-event-source';

const messages = ref([]);
const newMessage = ref('');
const messagesContainer = ref(null);
const useTypewriter = ref(false);

const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const sendMessage = async () => {
  const messageText = newMessage.value.trim();
  if (!messageText) return;

  // 添加用户消息
  messages.value.push({
    isUser: true,
    text: messageText
  });

  newMessage.value = '';
  await scrollToBottom();

  try {
    // 创建AI消息对象并添加到消息列表
    const aiMessage = ref({
      isUser: false,
      text: ''
    });
    messages.value.push(aiMessage.value);

    if (useTypewriter.value) {
      // 流式响应
      await fetchEventSource(`/v1/chat/stream`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          content: messageText
        }),
        onmessage(ev) {
          try {
            const data = ev.data.trim();
            if (!data) return;
            
            // 尝试解析JSON，如果失败则直接使用原始数据
            try {
              const jsonData = JSON.parse(data);
              aiMessage.value.text += jsonData.content;
            } catch {
              console.log('Failed to parse JSON, using raw data', data);
              aiMessage.value.text += data;
            }
            
            scrollToBottom();
          } catch (error) {
            console.error('处理消息时出错:', error);
          }
        },
        onclose() {
          console.log('Stream closed');
        },
        onerror(err) {
          console.error('Stream error:', err);
          throw err;
        }
      });
    } else {
      // 普通响应
      const response = await fetch(`/v1/chat/completions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          content: messageText
        })
      });

      if (!response.ok) {
        throw new Error('API请求失败');
      }

      const responseData = await response.json();
      aiMessage.value.text = responseData.content;
      await scrollToBottom();
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    messages.value.push({
      isUser: false,
      text: '抱歉，发生了一些错误，请稍后再试。'
    });
  }
  await scrollToBottom();
};

onMounted(() => {
  // 添加欢迎消息
  messages.value.push({
    isUser: false,
    text: '你好！我是AI助手，有什么可以帮你的吗？'
  });
});
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 800px;
  margin: 0 auto;
  background-color: white;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 0;
}

.input-container {
  border-top: 1px solid #e5e5e5;
  padding: 2rem 3rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background-color: white;
  margin-top: 0.5rem;
}

.controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.input-group {
  display: flex;
  gap: 1rem;
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #4f46e5;
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.switch-label {
  font-size: 0.875rem;
  color: #6b7280;
}

.message-input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  resize: none;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.5;
}

.message-input:focus {
  outline: none;
  border-color: #4f46e5;
}

.send-button {
  padding: 0.75rem 1.5rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-button:hover:not(:disabled) {
  background-color: #4338ca;
}

.send-button:disabled {
  background-color: #e5e5e5;
  cursor: not-allowed;
}
</style>