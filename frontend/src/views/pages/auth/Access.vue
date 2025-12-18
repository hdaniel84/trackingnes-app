<script setup>
import Button from 'primevue/button';
</script>
<template>
    <div class="flex flex-col items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-900 p-4">
        <div class="text-center">
            <div class="bg-red-100 dark:bg-red-900/20 p-4 rounded-full inline-flex mb-4">
                <i class="pi pi-lock text-4xl text-red-500"></i>
            </div>
            <h1 class="text-3xl font-bold text-surface-900 dark:text-surface-0 mb-2">Acesso Negado</h1>
            <p class="text-surface-600 dark:text-surface-400 mb-6 max-w-md mx-auto">
                Desculpe, você não tem as permissões necessárias para aceder a esta página.
                Se acredita que isto é um erro, contacte o seu administrador.
            </p>

            <Button label="Voltar ao Dashboard" icon="pi pi-home" @click="$router.push({ name: 'dashboard' })" />
        </div>
    </div>
</template>
