<template>
  <div class="left-panel">
    <canvas
        ref="canvas"
        @click="handleClick"
        width="450"
        height="450"
        style="display: block;"
    ></canvas>
  </div>
</template>

<script>
export default {
  props: {
    points: Array, // Массив точек
    radius: {
      type: Number,
      required: true,
    },
  },
  mounted() {
    this.drawGraph();
  },
  watch: {
    points: {
      handler() {
        this.drawGraph(); // Перерисовка точек
      },
      deep: true,
    },
    radius: {
      handler() {
        this.drawGraph(); // Перерисовка графика при изменении радиуса
      },
    },
  },
  methods: {
    drawGraph() {
      const canvas = this.$refs.canvas;
      const ctx = canvas.getContext("2d");
      const centerX = canvas.width / 2;
      const centerY = canvas.height / 2;

      const fixedR = 100; // Фиксированный размер областей
      const scaleFactor = fixedR / this.radius; // Масштаб для точек и рисков

      // Очистка канваса
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Рисование областей
      ctx.fillStyle = "#9966CC";

      // Прямоугольник (первая четверть)
      ctx.fillRect(centerX, centerY - fixedR / 2, fixedR, fixedR / 2);

      // Треугольник (вторая четверть)
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.lineTo(centerX - fixedR, centerY);
      ctx.lineTo(centerX, centerY - fixedR / 2);
      ctx.closePath();
      ctx.fill();

      // Четверть круга (четвертая четверть)
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.arc(centerX, centerY, fixedR, 0, Math.PI / 2);
      ctx.closePath();
      ctx.fill();

      // Рисование осей
      ctx.strokeStyle = "black";
      ctx.lineWidth = 2;

      // Ось Y
      ctx.beginPath();
      ctx.moveTo(centerX, 0);
      ctx.lineTo(centerX, canvas.height);
      ctx.stroke();

      // Ось X
      ctx.beginPath();
      ctx.moveTo(0, centerY);
      ctx.lineTo(canvas.width, centerY);
      ctx.stroke();

      // Стрелки на осях
      ctx.beginPath();
      ctx.moveTo(centerX, 0);
      ctx.lineTo(centerX - 5, 15);
      ctx.lineTo(centerX + 5, 15);
      ctx.closePath();
      ctx.fillStyle = "black";
      ctx.fill();

      ctx.beginPath();
      ctx.moveTo(canvas.width, centerY);
      ctx.lineTo(canvas.width - 15, centerY - 5);
      ctx.lineTo(canvas.width - 15, centerY + 5);
      ctx.closePath();
      ctx.fill();

      // Риски для R и R/2
      ctx.lineWidth = 1;

      // X-axis risks
      ctx.beginPath();
      ctx.moveTo(centerX + fixedR, centerY - 5); // R
      ctx.lineTo(centerX + fixedR, centerY + 5);
      ctx.moveTo(centerX + fixedR / 2, centerY - 5); // R/2
      ctx.lineTo(centerX + fixedR / 2, centerY + 5);
      ctx.moveTo(centerX - fixedR, centerY - 5); // -R
      ctx.lineTo(centerX - fixedR, centerY + 5);
      ctx.moveTo(centerX - fixedR / 2, centerY - 5); // -R/2
      ctx.lineTo(centerX - fixedR / 2, centerY + 5);
      ctx.stroke();

      // Y-axis risks
      ctx.beginPath();
      ctx.moveTo(centerX - 5, centerY - fixedR); // R
      ctx.lineTo(centerX + 5, centerY - fixedR);
      ctx.moveTo(centerX - 5, centerY - fixedR / 2); // R/2
      ctx.lineTo(centerX + 5, centerY - fixedR / 2);
      ctx.moveTo(centerX - 5, centerY + fixedR); // -R
      ctx.lineTo(centerX + 5, centerY + fixedR);
      ctx.moveTo(centerX - 5, centerY + fixedR / 2); // -R/2
      ctx.lineTo(centerX + 5, centerY + fixedR / 2);
      ctx.stroke();

      // Устанавливаем шрифт для текста
      ctx.font = "0.9rem 'Monocraft', sans-serif"; // Используем шрифт страницы
      ctx.fillStyle = "#000";
      ctx.textAlign = "center";

      // Метки осей (обновленные значения)
      ctx.fillText("Y", centerX + 10, 15);
      ctx.fillText("X", canvas.width - 15, centerY - 10);

      if (this.radius !== 100) {
        ctx.fillText(-this.radius, centerX - fixedR, centerY + 20);
        ctx.fillText(this.radius, centerX + fixedR, centerY + 20);
        ctx.fillText(-this.radius, centerX + 20, centerY + fixedR);
        ctx.fillText(this.radius, centerX + 20, centerY - fixedR);

        ctx.fillText(-(this.radius / 2), centerX - fixedR / 2, centerY + 20);
        ctx.fillText(this.radius / 2, centerX + fixedR / 2, centerY + 20);
        ctx.fillText(-(this.radius / 2), centerX + 30, centerY + fixedR / 2 + 5);
        ctx.fillText(this.radius / 2, centerX + 30, centerY - fixedR / 2);
      } else {
        ctx.fillText("-R", centerX - fixedR, centerY + 20);
        ctx.fillText("R", centerX + fixedR, centerY + 20);
        ctx.fillText("-R", centerX + 20, centerY + fixedR);
        ctx.fillText("R", centerX + 20, centerY - fixedR);

        ctx.fillText("-R/2", centerX - fixedR / 2, centerY + 20);
        ctx.fillText("R/2", centerX + fixedR / 2, centerY + 20);
        ctx.fillText("-R/2", centerX + 30, centerY + fixedR / 2 + 5);
        ctx.fillText("R/2", centerX + 30, centerY - fixedR / 2);
      }

      this.points.forEach((point) => {
        this.drawPoint(point.x, point.y, scaleFactor);
      });
    },

    drawPoint(x, y, scaleFactor) {
      const canvas = this.$refs.canvas;
      const ctx = canvas.getContext("2d");
      const centerX = canvas.width / 2;
      const centerY = canvas.height / 2;

      const pointX = centerX + x * scaleFactor;
      const pointY = centerY - y * scaleFactor;

      ctx.beginPath();
      ctx.arc(pointX, pointY, 5, 0, 2 * Math.PI);
      ctx.fillStyle = "red";
      ctx.fill();
    },

    handleClick(event) {
      if (this.radius === 100) {
        alert("Выберите радиус перед добавлением точки!");
        return;
      }

      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      const centerX = canvas.width / 2;
      const centerY = canvas.height / 2;

      const fixedR = 100;

      const x = ((clickX - centerX) / (fixedR / this.radius)).toFixed(2);
      const y = ((centerY - clickY) / (fixedR / this.radius)).toFixed(2);

      this.$emit("add-point", { x: parseFloat(x), y: parseFloat(y), hit: true });
    },
  },
};
</script>

<style scoped>
canvas {
  display: block;
}
</style>
