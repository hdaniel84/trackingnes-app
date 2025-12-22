<script setup>
import TrackingTabs from '@/components/forms/tracking/TrackingTabs.vue';
import TrackingList from '@/components/forms/tracking/TrackingList.vue';
import { usePhase } from '@/layout/composables/usePhase';
const { phaseId } = usePhase();
</script>

<template>
    <Fluid>
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-2 p-2">
            <div class="lg:col-span-9 flex">
                <TrackingTabs class="flex-1" />
            </div>

            <div class="lg:col-span-3 flex">
                <div class="sticky top-6 self-start max-h-[80vh]">
                    <TrackingList class="flex-1" :key="phaseId" />
                </div>
            </div>
        </div>

    </Fluid>
</template>