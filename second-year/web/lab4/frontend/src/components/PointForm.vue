<template>
  <div class="right-panel">
    <div class="form-field">
      <label class="label-x">Изменение X:</label>
      <button
          v-for="x in xValues"
          :key="x"
          class="change-x"
          @click="selectX(x)"
      >
        {{ x }}
      </button>
    </div>
    <div class="form-field">
      <label class="label-y" for="yCoord">Изменение Y (-5...5):</label>
      <input id="yCoord" type="number" v-model="y" />
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      xValues: [-3, -2, -1, 0, 1, 2, 3],
      y: null,
    };
  },
};
</script>
