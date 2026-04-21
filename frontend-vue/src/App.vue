 <template>
  <!-- Le router-view injectera Login.vue ou Dashboard.vue selon l'URL -->
  <router-view></router-view>
</template>

<script setup>
// Composant racine très simple pour le moment.
// Dans /dev6, nous ajouterons ici une barre de navigation globale (Navbar)
// qui s'affichera uniquement si l'utilisateur est authentifié.
</script>

<style>
/* Reset CSS basique */
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
