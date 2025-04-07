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
  padding: 1rem;
  gap: 1rem;
  border-bottom: 1px solid #e5e5e5;
}

.message-user {
  background-color: #f9fafb;
}

.message-ai {
  background-color: white;
}

.avatar {
  font-size: 1.5rem;
}

.content {
  flex: 1;
}

.name {
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.text {
  line-height: 1.5;
  white-space: pre-wrap;
}
</style>