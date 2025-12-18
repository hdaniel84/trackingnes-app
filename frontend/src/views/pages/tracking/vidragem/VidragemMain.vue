<script setup>
import VidragemTabs from '@/components/forms/tracking/Vidragem/VidragemTabs.vue';
import VidragemList from '@/components/forms/tracking/Vidragem/VidragemList.vue';
</script>

<template>
    <Fluid>
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 p-6 lg:items-stretch">
            <div class="lg:col-span-9 flex">
                <VidragemTabs class="flex-1"/>
            </div>

            <div class="lg:col-span-3 flex">
                <VidragemList class="flex-1" v-can="'READ_VIDRAGEM'" />
            </div>
        </div>

    </Fluid>
</template>