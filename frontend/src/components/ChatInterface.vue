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
        <button @click="showDrawer = true" class="settings-button">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="3"></circle>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- 设置抽屉 -->
    <div class="drawer-overlay" v-if="showDrawer" @click="showDrawer = false"></div>
    <div class="settings-drawer" :class="{ 'drawer-open': showDrawer }">
      <div class="drawer-header">
        <h3>聊天设置</h3>
        <button @click="showDrawer = false" class="close-button">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
      <div class="drawer-content">
        <div class="setting-item">
          <label class="setting-label">
            <span>对话标识</span>
            <div class="conversation-id">
              {{ conversationId }}
            </div>
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>流式响应</span>
            <label class="switch">
              <input type="checkbox" v-model="useTypewriter">
              <span class="slider"></span>
            </label>
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>选择模型</span>
            <select v-model="selectedModel" class="model-select" :disabled="!models.length">
              <option v-for="model in models" :key="model.modelId" :value="model.modelId">
                {{ model.modelName }}
              </option>
            </select>
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>温度 ({{ temperature }})</span>
            <input
              type="range"
              v-model="temperature"
              class="temperature-slider"
              min="0"
              max="1"
              step="0.05"
            />
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>随机种子</span>
            <input
              type="number"
              v-model.number="seed"
              class="number-input"
              min="0"
              max="1000000"
            />
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>Top P</span>
            <input
              type="number"
              v-model.number="topP"
              class="number-input"
              min="0"
              max="1"
              step="0.1"
            />
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>Top K</span>
            <input
              type="number"
              v-model.number="topK"
              class="number-input"
              min="1"
              max="100"
              step="1"
            />
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>联网查询</span>
            <label class="switch">
              <input type="checkbox" v-model="enableSearch">
              <span class="slider"></span>
            </label>
          </label>
        </div>
        <div class="setting-item">
          <label class="setting-label">
            <span>停用词</span>
            <div class="stop-words-input">
              <input
                type="text"
                v-model="newStopWord"
                @keyup.enter="addStopWord"
                placeholder="输入停用词并按回车"
                class="text-input"
              />
            </div>
          </label>
          <div class="stop-words-list">
            <div v-for="(word, index) in stopWords" :key="index" class="stop-word-item">
              {{ word }}
              <button @click="removeStopWord(index)" class="remove-button">×</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import ChatMessage from './ChatMessage.vue';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { ulid } from 'ulid';

const messages = ref([]);
const newMessage = ref('');
const messagesContainer = ref(null);
const useTypewriter = ref(false);
const enableSearch = ref(false);
const models = ref([]);
const selectedModel = ref('');
const stopWords = ref([]);
const temperature = ref(0.85);
const seed = ref();
const topP = ref(0.8);
const topK = ref(50);
const showDrawer = ref(false);
const conversationId = ref(ulid());

const fetchModels = async () => {
  try {
    const response = await fetch('/v1/models');
    if (!response.ok) throw new Error('获取模型列表失败');
    models.value = await response.json();
    if (models.value.length > 0) {
      selectedModel.value = models.value[0].modelId;
    }
  } catch (error) {
    console.error('获取模型列表失败:', error);
  }
};

const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const sendMessage = async () => {
  const messageText = newMessage.value.trim();
  if (!messageText || !selectedModel.value) return;

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
          content: messageText,
          conversationId: conversationId.value,
          modelId: selectedModel.value,
          temperature: temperature.value,
          seed: seed.value,
          topP: topP.value,
          topK: topK.value,
          enableSearch: enableSearch.value,
          stop: stopWords.value
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
          content: messageText,
          conversationId: conversationId.value,
          modelId: selectedModel.value,
          temperature: temperature.value,
          seed: seed.value,
          topP: topP.value,
          topK: topK.value,
          enableSearch: enableSearch.value,
          stop: stopWords.value
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
  fetchModels();
  // 生成新的对话ID
  conversationId.value = ulid();
  // 添加欢迎消息
  messages.value.push({
    isUser: false,
    text: '你好！我是AI助手，有什么可以帮你的吗？'
  });
});
const newStopWord = ref('');

const addStopWord = () => {
  const word = newStopWord.value.trim();
  if (word && !stopWords.value.includes(word)) {
    stopWords.value.push(word);
    newStopWord.value = '';
  }
};

const removeStopWord = (index) => {
  stopWords.value.splice(index, 1);
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 1000px;
  margin: 0 auto;
  background-color: white;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
}

.input-container {
  border-top: 1px solid #e5e5e5;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background-color: white;
}

.controls {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  background-color: #f8fafc;
  border-radius: 8px;
}

.input-group {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
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

.model-select {
  padding: 0.5rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #374151;
  background-color: white;
  width: 80px;
}

.model-select:focus {
  outline: none;
  border-color: #4f46e5;
}

.model-select:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.temperature-input {
  padding: 0.5rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #374151;
  background-color: white;
  width: 80px;
}

.temperature-input:focus {
  outline: none;
  border-color: #4f46e5;
}

.settings-button {
  padding: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.settings-button:hover {
  color: #4f46e5;
}

.drawer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 40;
}

.settings-drawer {
  position: fixed;
  top: 0;
  right: -400px;
  width: 400px;
  height: 100vh;
  background-color: white;
  box-shadow: -4px 0 6px -1px rgba(0, 0, 0, 0.1);
  transition: right 0.3s ease-in-out;
  z-index: 50;
  display: flex;
  flex-direction: column;
}

.drawer-open {
  right: 0;
}

.drawer-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drawer-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #6b7280;
  padding: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.close-button:hover {
  color: #4f46e5;
}

.drawer-content {
  padding: 1.5rem;
  flex: 1;
  overflow-y: auto;
}

.setting-item {
  margin-bottom: 1.5rem;
}

.setting-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
  color: #374151;
}

.temperature-slider {
  width: 200px;
  margin-top: 0.5rem;
}

.number-input {
  width: 80px;
  padding: 0.5rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #374151;
  background-color: white;
}

.number-input:focus {
  outline: none;
  border-color: #4f46e5;
}

.model-select {
  padding: 0.5rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #374151;
  background-color: white;
  width: 200px;
}

.model-select:focus {
  outline: none;
  border-color: #4f46e5;
}

.stop-words-input {
  margin-top: 0.5rem;
  width: 100%;
}

.text-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #374151;
}

.text-input:focus {
  outline: none;
  border-color: #4f46e5;
}

.stop-words-list {
  margin-top: 0.5rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.stop-word-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background-color: #f3f4f6;
  border-radius: 4px;
  font-size: 0.875rem;
}

.conversation-id {
  padding: 0.5rem;
  background-color: #f3f4f6;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.875rem;
  color: #4f46e5;
  user-select: all;
}

.remove-button {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 0 0.25rem;
}

.remove-button:hover {
  color: #ef4444;
}
</style>