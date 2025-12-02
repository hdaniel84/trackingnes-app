<script setup>
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import { reactive, ref } from 'vue';
import { useAuthStore } from '@/stores/auth'; 
import { useToast } from 'primevue/usetoast'; 

// --- Imports de Componentes ---
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import Message from 'primevue/message';
import myLogo from '@/assets/images/logo_webapps.png';
// ---------------------------------------------------------------------

const authStore = useAuthStore();
const toast = useToast();

// 1. Estado para as credenciales
const credentials = reactive({
    username: '', 
    password: '',
});

// 2. Estado de la UI
const loginError = ref('');
const isLoading = ref(false);
const checked = ref(false); 

// 3. Función de Login
const handleLogin = async () => {
    loginError.value = ''; 
    isLoading.value = true;

    try {
        await authStore.login(credentials);
    } catch (error) {
        const errorMessage = error.message || 'Erro desconhecido ao iniciar sessão. Verifique o backend.';
        loginError.value = errorMessage;

        toast.add({
            severity: 'error',
            summary: 'Erro de Acesso',
            detail: errorMessage,
            life: 5000
        });

    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
    <div class="min-h-screen flex items-center justify-center overflow-hidden bg-surface-50 dark:bg-surface-950">
        <FloatingConfigurator />
        
        <div class="w-full h-screen grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12">
            
            <div class="hidden md:flex lg:col-span-5 flex-col justify-between p-10 relative overflow-hidden bg-blue-900">
                <div class="absolute inset-0 bg-gradient-to-br from-blue-600 to-black opacity-90 z-0"></div>
                
                <div class="absolute -top-24 -left-24 w-96 h-96 rounded-full bg-blue-600 opacity-10 blur-3xl z-0"></div>
                <div class="absolute bottom-10 right-10 w-80 h-80 rounded-full bg-blue-700 opacity-10 blur-3xl z-0"></div>

                <div class="relative z-10">
                    <div class="flex items-center gap-3 font-bold text-2xl text-white mb-10">
                         <i class="pi pi-chart-bar text-3xl"></i>
                         <span>TrackingNes</span>
                    </div>
                </div>

                <div class="relative z-10 text-white mb-10">
                    <p class="text-4xl font-bold mb-4 leading-tight">Gestão de rastreio de produção</p>
                    <p class="text-surface-300 text-lg">Aceda ao painel de controlo para supervisionar cada etapa da produção.</p>
                </div>
                
                <div class="relative z-10 text-surface-500 text-sm">
                    &copy; {{ new Date().getFullYear() }} Mesacer Webapps
                </div>
            </div>

            <div class="col-span-1 lg:col-span-7 flex items-center justify-center p-6 md:p-12 bg-surface-100 dark:bg-surface-900">
                
                <div class="w-full max-w-md bg-surface-0 dark:bg-surface-800 p-8 rounded-2xl shadow-2xl border border-surface-200 dark:border-surface-700">
                    
                    <div class="text-center mb-8">
                        <img :src="myLogo" alt="Webapps" class="h-14 mb-4 mx-auto" />
                        <h2 class="text-surface-900 dark:text-surface-0 text-2xl font-bold mb-2">Bem-vindo de volta</h2>
                        <p class="text-muted-color text-sm">Por favor, introduza os seus dados para continuar.</p>
                    </div>

                    <form @submit.prevent="handleLogin">
                        <Message v-if="loginError" severity="error" :closable="false" class="mb-6 shadow-sm">
                            {{ loginError }}
                        </Message>

                        <div class="flex flex-col gap-5">
                            <div class="flex flex-col gap-2">
                                <label for="username1" class="text-surface-700 dark:text-surface-300 font-semibold text-sm">Utilizador</label>
                                <span class="p-input-icon-left">
                                    <InputText 
                                        id="username1" 
                                        type="text" 
                                        placeholder="ex. nome.utilizador" 
                                        class="w-full p-3" 
                                        v-model="credentials.username" 
                                        :disabled="isLoading" 
                                        required 
                                    />
                                </span>
                            </div>

                            <div class="flex flex-col gap-2">
                                <div class="flex justify-between items-center">
                                    <label for="password1" class="text-surface-700 dark:text-surface-300 font-semibold text-sm">Palavra-passe</label>
                                    <a class="text-primary-600 hover:text-primary-700 font-semibold text-sm cursor-pointer no-underline transition-colors duration-200">
                                        Esqueceu-se da palavra-passe?
                                    </a>
                                </div>
                                <Password 
                                    id="password1" 
                                    v-model="credentials.password" 
                                    placeholder="••••••••" 
                                    :toggleMask="true" 
                                    class="w-full" 
                                    inputClass="w-full p-3"
                                    :feedback="false" 
                                    :disabled="isLoading" 
                                    required
                                >
                                </Password>
                            </div>

                            <div class="flex items-center mt-2">
                                <Checkbox v-model="checked" id="rememberme1" binary class="mr-2"></Checkbox>
                                <label for="rememberme1" class="text-surface-600 dark:text-surface-400 cursor-pointer text-sm">Manter sessão iniciada</label>
                            </div>

                            <Button 
                                type="submit" 
                                label="Iniciar Sessão" 
                                class="w-full p-3 font-bold mt-2 shadow-md transition-transform active:scale-95" 
                                :loading="isLoading" 
                                :disabled="isLoading" 
                            />
                        </div>
                    </form>
                    
                    <div class="mt-8 text-center text-surface-500 text-sm">
                        Não tem uma conta? <span class="text-primary font-bold cursor-pointer hover:underline">Contacte o suporte</span>
                    </div>
                </div> </div>
        </div>
    </div>
</template>

<style scoped>
/* Ajustes para o componente Password preencher a largura */
:deep(.p-password) {
    width: 100%;
}
:deep(.p-password-input) {
    width: 100%;
}

/* Foco suave */
:deep(.p-inputtext:focus) {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(var(--primary-color-rgb), 0.2);
}
</style>