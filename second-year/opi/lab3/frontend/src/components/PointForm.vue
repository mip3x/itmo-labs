<template>
  <div class="right-panel">
    <div class="form-field">
      <label for="x-coord" class="label-x">Выберите X:</label>
      <div id="x-buttons" class="x-buttons">
        <button
            v-for="x in xValues"
            :key="x"
            class="change-x"
            :data-test="'x-btn-' + x"
            :class="{ active: x === selectedX }"
            @click="setX(x)"
        >
          {{ x }}
        </button>
      </div>
    </div>

    <div class="form-field">
      <label for="y-coord" class="label-y">Введите Y:</label>
      <input
          id="plot-y"
          v-model.number="yCoord"
          class="input-y"
          type="number"
          step="0.1"
          min="-5"
          max="5"
          placeholder="-5...5"
      />
    </div>

    <div class="form-field">
      <label for="radius" class="label-r">Выберите R:</label>
      <div id="r-buttons" class="x-buttons">
        <button
            v-for="r in rValues"
            :key="r"
            class="change-x"
            :data-test="'r-btn-' + r"
            :class="{ active: r === selectedR }"
            @click="setR(r)"
        >
          {{ r }}
        </button>
      </div>
    </div>

    <button id="plot-submit" class="check-point-button active" @click="checkPoint">
      Проверить точку
    </button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      xValues: [-3, -2, -1, 0, 1, 2, 3, 4, 5],
      rValues: [1, 2, 3, 4, 5],
      selectedX: null,
      yCoord: '',
      selectedR: null,
    };
  },
  methods: {
    setX(x) {
      this.selectedX = x;
    },

    setR(r) {
      this.selectedR = r;
      this.$emit('update-radius', r);
    },

    checkPoint() {
      if (this.selectedX === null || this.yCoord === '' || this.selectedR === null) {
        alert('Заполните все поля!');
        return;
      }

      if (isNaN(this.yCoord) || this.yCoord < -5 || this.yCoord > 5) {
        alert("Некорректное значение Y. Введите число в диапазоне от -5 до 5!");
        return;
      }

      this.$emit('add-point', {
        x: this.selectedX,
        y: parseFloat(this.yCoord),
        r: this.selectedR,
      });
    },
  },
};
</script>
