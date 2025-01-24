<template>
  <div class="results-table">
    <h2>Результаты</h2>
    <table>
      <thead>
      <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Результат</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="point in points" :key="point.id">
        <td>{{ point.x }}</td>
        <td>{{ point.y }}</td>
        <td>{{ point.radius }}</td>
        <td :style="{ color: point.color }">
          {{ point.result ? '✔' : '❌' }}
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: {
    points: Array,
  },
};
</script>
