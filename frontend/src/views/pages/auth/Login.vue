<script setup>
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import { reactive, ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'primevue/usetoast';

// --- PrimeVue Imports ---
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import Message from 'primevue/message';
import FloatLabel from 'primevue/floatlabel';
import IconField from 'primevue/iconfield';
import InputIcon from 'primevue/inputicon';

// --- Assets ---
import myLogo from '@/assets/images/logo_webapps.png';
import mesaLogo from '@/assets/images/logo_mesa_white.svg';

const authStore = useAuthStore();
const toast = useToast();

const credentials = reactive({ username: '', password: '' });
const loginError = ref('');
const isLoading = ref(false);
const checked = ref(false);
const showContent = ref(false); // Para animación de entrada

onMounted(() => {
    // Pequeño delay para animar la entrada del formulario
    setTimeout(() => { showContent.value = true; }, 100);
});

const handleLogin = async () => {
    loginError.value = '';
    isLoading.value = true;

    try {
        await authStore.login(credentials);
    } catch (error) {
        const errorMessage = error.message || 'Erro de conexão.';
        loginError.value = errorMessage;

        // Animación de "sacudida" en el formulario si falla (opcional visual)
        const form = document.getElementById('login-form-card');
        if (form) {
            form.classList.add('shake');
            setTimeout(() => form.classList.remove('shake'), 500);
        }

        toast.add({
            severity: 'error',
            summary: 'Acesso Negado',
            detail: errorMessage,
            life: 4000
        });
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
    <div class="min-h-screen flex w-full overflow-hidden bg-surface-50 dark:bg-surface-950 font-sans">
        <div class="fixed top-4 right-4 z-50">
            <FloatingConfigurator />
        </div>

        <div
            class="hidden lg:flex lg:w-[45%] relative flex-col justify-between p-12 text-white overflow-hidden bg-primary-900">

            <div class="absolute inset-0 z-0">
                <div class="absolute inset-0 bg-gradient-to-br from-primary-800 via-primary-900 to-black opacity-90">
                </div>
                <div class="animated-blob blob-1"></div>
                <div class="animated-blob blob-2"></div>
                <div
                    class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay">
                </div>
            </div>

            <div class="relative z-10 h-full flex flex-col justify-between">
                <div class="flex items-center gap-3 animate-fade-in-down">
                    <img :src="mesaLogo" alt="Mesacer Ceramics" class="h-8" />
                    <div class="h-6 w-px bg-white/30"></div>
                    <span class="font-mono text-sm tracking-widest uppercase opacity-80">Tracking System</span>
                </div>

                <div class="max-w-md animate-fade-in-up">
                    <div
                        class="w-16 h-16 rounded-2xl bg-white/10 backdrop-blur-md flex items-center justify-center mb-6 border border-white/20 shadow-lg">
                        <i class="pi pi-chart-bar text-3xl text-white"></i>
                    </div>
                    <h1 class="text-5xl font-bold mb-6 leading-tight tracking-tight">
                        Controlo total da <span class="text-primary-300">produção.</span>
                    </h1>
                    <p class="text-lg text-primary-100/80 leading-relaxed font-light">
                        Monitorização em tempo real, rastreabilidade fase a fase e gestão de eficiência numa única
                        plataforma.
                    </p>
                </div>

                <div class="text-xs text-primary-200/50 uppercase tracking-widest font-semibold">
                    &copy; {{ new Date().getFullYear() }} Mesacer TrackingNes v2.0
                </div>
            </div>
        </div>

        <div class="flex-1 flex items-center justify-center p-6 relative">
            <div
                class="absolute inset-0 lg:hidden bg-gradient-to-b from-primary-900/10 to-transparent pointer-events-none">
            </div>

            <div id="login-form-card" class="w-full max-w-[420px] transition-all duration-700 ease-out transform"
                :class="showContent ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'">
                <div
                    class="bg-surface-0 dark:bg-surface-900 p-8 md:p-10 rounded-3xl shadow-[0_20px_60px_-15px_rgba(0,0,0,0.1)] border border-surface-100 dark:border-surface-800">

                    <div class="text-center mb-10">
                        <img :src="myLogo" alt="Webapps"
                            class="h-12 mx-auto mb-6 hover:scale-105 transition-transform duration-300" />
                        <h2 class="text-2xl font-bold text-surface-900 dark:text-surface-0">Bem-vindo de volta</h2>
                        <p class="text-surface-500 dark:text-surface-400 text-sm mt-2">Insira as suas credenciais para
                            aceder</p>
                    </div>

                    <form @submit.prevent="handleLogin" class="flex flex-col gap-6">

                        <transition name="p-message" tag="div">
                            <Message v-if="loginError" severity="error" :closable="false" class="mb-2">
                                {{ loginError }}
                            </Message>
                        </transition>

                        <div class="field">
                            <FloatLabel>
                                <IconField>
                                    <InputIcon class="pi pi-user z-10" />
                                    <InputText id="username" v-model="credentials.username" class="w-full pl-10"
                                        :disabled="isLoading" />
                                </IconField>
                                <label for="username" class="ml-8">Nome de Utilizador</label>
                            </FloatLabel>
                        </div>

                        <div class="field">
                            <FloatLabel>
                                <IconField>
                                    <InputIcon class="pi pi-lock z-10" />
                                    <Password id="password" v-model="credentials.password" :feedback="false" toggleMask
                                        class="w-full" inputClass="w-full pl-10" :disabled="isLoading" />
                                </IconField>
                                <label for="password" class="ml-8">Palavra-passe</label>
                            </FloatLabel>
                            <div class="flex justify-end mt-2">
                                <a href="#"
                                    class="text-xs font-semibold text-primary-600 hover:text-primary-700 no-underline hover:underline transition-all">
                                    Recuperar palavra-passe?
                                </a>
                            </div>
                        </div>

                        <div class="flex items-center justify-between mt-2">
                            <div class="flex items-center gap-2">
                                <Checkbox v-model="checked" id="remember" binary />
                                <label for="remember"
                                    class="text-sm text-surface-600 dark:text-surface-300 cursor-pointer select-none">
                                    Lembrar-me
                                </label>
                            </div>
                        </div>

                        <Button type="submit" :loading="isLoading"
                            class="w-full py-3.5 font-bold text-base rounded-xl shadow-lg shadow-primary-500/30 hover:shadow-primary-500/50 transition-all duration-300 transform active:scale-[0.98]">
                            <span v-if="!isLoading">Iniciar Sessão</span>
                            <span v-else>A Autenticar...</span>
                        </Button>
                    </form>
                </div>

                <div class="text-center mt-8 text-xs text-surface-400 dark:text-surface-500">
                    Precisa de acesso? <span
                        class="font-bold text-surface-900 dark:text-surface-200 cursor-pointer hover:underline">Contacte
                        o Administrador</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 1. Animated Blobs for Background */
.animated-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.6;
    animation: float 10s infinite ease-in-out;
}

.blob-1 {
    top: -10%;
    left: -10%;
    width: 500px;
    height: 500px;
    background: #3B82F6;
    /* Primary Blue */
    animation-delay: 0s;
}

.blob-2 {
    bottom: -10%;
    right: -10%;
    width: 400px;
    height: 400px;
    background: #6366f1;
    /* Indigo */
    animation-delay: -5s;
}

@keyframes float {
    0% {
        transform: translate(0, 0) scale(1);
    }

    33% {
        transform: translate(30px, -50px) scale(1.1);
    }

    66% {
        transform: translate(-20px, 20px) scale(0.9);
    }

    100% {
        transform: translate(0, 0) scale(1);
    }
}

/* 2. Shake Animation on Error */
.shake {
    animation: shake 0.5s cubic-bezier(.36, .07, .19, .97) both;
}

@keyframes shake {

    10%,
    90% {
        transform: translate3d(-1px, 0, 0);
    }

    20%,
    80% {
        transform: translate3d(2px, 0, 0);
    }

    30%,
    50%,
    70% {
        transform: translate3d(-4px, 0, 0);
    }

    40%,
    60% {
        transform: translate3d(4px, 0, 0);
    }
}

/* 3. Text Animations */
.animate-fade-in-down {
    animation: fadeInDown 0.8s ease-out forwards;
}

.animate-fade-in-up {
    animation: fadeInUp 0.8s ease-out 0.2s forwards;
    /* delay */
    opacity: 0;
}

@keyframes fadeInDown {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 4. PrimeVue Overrides for "Pro" look */
:deep(.p-inputtext) {
    border-radius: 0.75rem;
    /* Rounded-xl */
    padding-top: 1rem;
    padding-bottom: 1rem;
    background: var(--surface-50);
    border: 1px solid var(--surface-200);
    transition: all 0.2s;
}

:deep(.dark) :deep(.p-inputtext) {
    background: var(--surface-800);
    border-color: var(--surface-700);
}

:deep(.p-inputtext:enabled:focus) {
    background: var(--surface-0);
    border-color: var(--primary-color);
    box-shadow: 0 0 0 4px rgba(var(--primary-color-rgb), 0.1);
    /* Suave glow */
}

:deep(.p-password-input) {
    width: 100%;
}

:deep(.p-float-label label) {
    margin-top: -0.5rem;
    /* Ajuste fino para centrar label */
}
</style>