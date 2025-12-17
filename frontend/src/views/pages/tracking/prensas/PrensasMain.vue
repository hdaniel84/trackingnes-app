<script setup>
import PrensasTabs from '@/components/forms/tracking/prensas/PrensasTabs.vue';
import PrensasList from '@/components/forms/tracking/prensas/PrensasList.vue';
</script>

<template>
    <Fluid>
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 p-6 lg:items-stretch">
            <div class="lg:col-span-9 flex">
                <PrensasTabs class="flex-1" />
            </div>

            <div class="lg:col-span-3 flex">
                <PrensasList class="flex-1" />
            </div>
        </div>

    </Fluid>
</template>