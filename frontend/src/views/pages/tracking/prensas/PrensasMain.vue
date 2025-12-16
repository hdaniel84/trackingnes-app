<script setup>
import PrensasTabs from '@/components/forms/tracking/prensas/PrensasTabs.vue';
import PrensasList from '@/components/forms/tracking/prensas/PrensasList.vue';
</script>

<template>
    <Fluid>
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 p-6">
            
            <div class="lg:col-span-9">
                <PrensasTabs />
            </div>

            <div class="lg:col-span-3">
                <PrensasList />
            </div>
            
        </div>
    </Fluid>
</template>