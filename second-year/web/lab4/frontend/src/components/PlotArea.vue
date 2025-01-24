<template>
  <div class="left-panel">
    <img
        src="/assets/background_aquatic.png"
        alt="Plot"
        @click="handleClick"
        style="max-width: 100%; height: auto;"
    />
  </div>
</template>

<script>
export default {
  methods: {
    handleClick(event) {
      const { offsetX, offsetY } = event;
      console.log('Click:', { offsetX, offsetY });
    },
  },
};
</script>
