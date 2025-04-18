<template>
  <div class="message" :class="{ 'message-user': isUser, 'message-ai': !isUser }">
    <div class="avatar">
      {{ isUser ? '👤' : '🤖' }}
    </div>
    <div class="content">
      <div class="name">{{ isUser ? 'You' : 'AI' }}</div>
      <div class="text" v-html="renderedText"></div>
    </div>
  </div>
</template>

<script setup>
import { marked } from 'marked';
import { computed } from 'vue';

const props = defineProps({
  isUser: {
    type: Boolean,
    default: false
  },
  text: {
    type: String,
    required: true
  }
});

const renderedText = computed(() => {
  return marked(props.text);
});
</script>

<style scoped>
.message {
  display: flex;
  padding: 1.5rem;
  gap: 1.5rem;
  border-bottom: 1px solid #edf2f7;
}

.message-user {
  background-color: #f8fafc;
}

.message-ai {
  background-color: white;
}

.avatar {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e2e8f0;
  border-radius: 50%;
  font-size: 1.25rem;
}

.content {
  flex: 1;
  background-color: #fff;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.name {
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #1a202c;
}

.text {
  line-height: 1.6;
  white-space: pre-wrap;
  color: #4a5568;
}
</style>