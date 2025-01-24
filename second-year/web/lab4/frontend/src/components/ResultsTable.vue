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
      <tr v-for="result in results" :key="result.id">
        <td>{{ result.x }}</td>
        <td>{{ result.y }}</td>
        <td>{{ result.r }}</td>
        <td>{{ result.result ? '✔' : '❌' }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      results: [
        { id: 1, x: 1, y: 2, r: 3, result: true },
        { id: 2, x: -1, y: -2, r: 4, result: false },
      ],
    };
  },
};
</script>
