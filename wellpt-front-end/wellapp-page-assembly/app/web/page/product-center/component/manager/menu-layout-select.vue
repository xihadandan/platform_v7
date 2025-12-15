<template>
  <div class="menu-layout-select">
    <!-- 模块导航菜单在侧边栏的情况 -->
    <div :class="config.menuPosition == 'sider' ? 'selected' : ''" :style="divStyle">
      <svg
        width="120.000000"
        height="80.000000"
        viewBox="0 0 82 52"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        v-if="siderToTop"
        @click="onSelect('siderTopMiddleBottom', 'sider', false)"
        :style="selectStyle"
      >
        <desc>垂直导航-置顶</desc>
        <defs />
        <g>
          <path
            style="fill: #ffffff"
            d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
          />
          <path style="fill: #333333" d="M12,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h10V52z" />
          <path
            style="fill: #ffffff"
            d="M9.5,11h-7C2.224,11,2,10.776,2,10.5v-2C2,8.224,2.224,8,2.5,8h7C9.776,8,10,8.224,10,8.5v2
		C10,10.776,9.776,11,9.5,11z"
          />
          <path
            style="fill: #ffffff"
            d="M9.5,17h-7C2.224,17,2,16.776,2,16.5v-2C2,14.224,2.224,14,2.5,14h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,16.776,9.776,17,9.5,17z"
          />
          <path
            style="fill: #ffffff"
            d="M9.5,23h-7C2.224,23,2,22.776,2,22.5v-2C2,20.224,2.224,20,2.5,20h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,22.776,9.776,23,9.5,23z"
          />
          <path
            style="fill: #ffffff"
            d="M9.5,29h-7C2.224,29,2,28.776,2,28.5v-2C2,26.224,2.224,26,2.5,26h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,28.776,9.776,29,9.5,29z"
          />
          <g id="替换色1_00000124137516637139799600000009407192016902889634_">
            <path style="fill: var(--w-primary-color)" d="M82,8H12V0h68c1.105,0,2,0.895,2,2V8z" />
          </g>
        </g>
      </svg>
      <svg
        v-else
        @click="onSelect('topMiddleSiderBottom', 'sider', false)"
        width="120.000000"
        height="80.000000"
        viewBox="0 0 82 52"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        :style="selectStyle"
      >
        <desc>垂直导航</desc>
        <defs />
        <g>
          <path
            style="display: inline; fill: #ffffff"
            d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
          C82,51.105,81.105,52,80,52z"
          />
          <path style="display: inline; fill: #333333" d="M12,52H2c-1.105,0-2-0.895-2-2V8h12V52z" />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,14h-7C2.224,14,2,13.776,2,13.5v-2C2,11.224,2.224,11,2.5,11h7
          c0.276,0,0.5,0.224,0.5,0.5v2C10,13.776,9.776,14,9.5,14z"
          />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,20h-7C2.224,20,2,19.776,2,19.5v-2C2,17.224,2.224,17,2.5,17h7
          c0.276,0,0.5,0.224,0.5,0.5v2C10,19.776,9.776,20,9.5,20z"
          />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,26h-7C2.224,26,2,25.776,2,25.5v-2C2,23.224,2.224,23,2.5,23h7
          c0.276,0,0.5,0.224,0.5,0.5v2C10,25.776,9.776,26,9.5,26z"
          />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,32h-7C2.224,32,2,31.776,2,31.5v-2C2,29.224,2.224,29,2.5,29h7
          c0.276,0,0.5,0.224,0.5,0.5v2C10,31.776,9.776,32,9.5,32z"
          />
          <g id="替换色2" style="display: inline">
            <path style="fill: var(--w-primary-color)" d="M80,0H2C0.895,0,0,0.895,0,2v6h82V2C82,0.895,81.105,0,80,0z" />
          </g>
        </g>
      </svg>
    </div>
    <div
      :class="config.menuPosition == 'header' && !config.appMenusToSider ? 'selected' : ''"
      @click="onSelect('topMiddleBottom', 'header', false)"
      :style="divStyle"
    >
      <svg
        v-if="config.topMenuBar"
        width="120.000000"
        height="80.000000"
        viewBox="0 0 82 52"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        :style="selectStyle"
      >
        <desc>水平导航_x2B_独立菜单</desc>
        <defs />
        <g>
          <path
            style="display: inline; fill: #ffffff"
            d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
          />
          <g id="替换色2" style="display: inline">
            <path style="fill: var(--w-primary-color)" d="M80,0H2C0.895,0,0,0.895,0,2v6h82V2C82,0.895,81.105,0,80,0z" />
          </g>
          <rect y="8" style="display: inline; fill: #e5e5e5" width="82" height="4" />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M29.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C30,10.824,29.776,11.048,29.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M37.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C38,10.824,37.776,11.048,37.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M45.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C46,10.824,45.776,11.048,45.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M53.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C54,10.824,53.776,11.048,53.5,11.048z"
          />
        </g>
      </svg>
      <svg
        width="120.000000"
        height="80.000000"
        viewBox="0 0 82 52"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        :style="selectStyle"
        v-else
      >
        <desc>水平导航</desc>
        <defs />
        <g>
          <path
            style="fill: #ffffff"
            d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
          />
          <g id="替换色1">
            <path style="fill: var(--w-primary-color)" d="M80,0H2C0.895,0,0,0.895,0,2v6h82V2C82,0.895,81.105,0,80,0z" />
          </g>
          <path
            style="fill: #ffffff"
            d="M10.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C11,5.437,10.776,5.661,10.5,5.661z"
          />
          <path
            style="fill: #ffffff"
            d="M21.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C22,5.437,21.776,5.661,21.5,5.661z"
          />
          <path
            style="fill: #ffffff"
            d="M32.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C33,5.437,32.776,5.661,32.5,5.661z"
          />
          <path
            style="fill: #ffffff"
            d="M43.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C44,5.437,43.776,5.661,43.5,5.661z"
          />
        </g>
      </svg>
    </div>
    <!-- 模块导航在头部的情况，模块子导航在侧边栏 -->
    <div :class="config.menuPosition == 'header' && config.appMenusToSider ? 'selected' : ''" :style="divStyle">
      <svg
        v-if="config.topMenuBar"
        width="120.000000"
        height="80.000000"
        viewBox="0 0 82 52"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        @click="onSelect('topMiddleSiderBottom', 'header', true)"
        :style="selectStyle"
      >
        <desc>混合型导航_x2B_独立菜单</desc>
        <defs />
        <g>
          <path
            style="display: inline; fill: #ffffff"
            d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
          />
          <g id="替换色3" style="display: inline">
            <path style="fill: var(--w-primary-color)" d="M80,0H2C0.895,0,0,0.895,0,2v6h82V2C82,0.895,81.105,0,80,0z" />
          </g>
          <path style="display: inline; fill: #333333" d="M2,52h10V12H0v38C0,51.105,0.895,52,2,52z" />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,18h-7C2.224,18,2,17.776,2,17.5v-2C2,15.224,2.224,15,2.5,15h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,17.776,9.776,18,9.5,18z"
          />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,24h-7C2.224,24,2,23.776,2,23.5v-2C2,21.224,2.224,21,2.5,21h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,23.776,9.776,24,9.5,24z"
          />
          <path
            style="display: inline; fill: #ffffff"
            d="M9.5,30h-7C2.224,30,2,29.776,2,29.5v-2C2,27.224,2.224,27,2.5,27h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,29.776,9.776,30,9.5,30z"
          />
          <rect y="8" style="display: inline; fill: #e5e5e5" width="82" height="4" />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M29.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C30,10.824,29.776,11.048,29.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M37.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C38,10.824,37.776,11.048,37.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M45.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C46,10.824,45.776,11.048,45.5,11.048z"
          />
          <path
            style="display: inline; fill: var(--w-primary-color)"
            d="M53.5,11.048h-5c-0.276,0-0.5-0.224-0.5-0.5v-1c0-0.276,0.224-0.5,0.5-0.5h5
		c0.276,0,0.5,0.224,0.5,0.5v1C54,10.824,53.776,11.048,53.5,11.048z"
          />
        </g>
      </svg>
      <template v-else>
        <svg
          v-if="siderToTop"
          @click="onSelect('siderTopMiddleBottom', 'header', true)"
          width="120.000000"
          height="80.000000"
          viewBox="0 0 82 52"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          :style="selectStyle"
        >
          <desc>混合型导航-置顶</desc>
          <defs />
          <g>
            <path
              style="display: inline; fill: #ffffff"
              d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
            />
            <g id="替换色2" style="display: inline">
              <path style="fill: var(--w-primary-color)" d="M82,0H12v8h70V0z" />
            </g>
            <path
              style="display: inline; fill: #ffffff"
              d="M22.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C23,5.437,22.776,5.661,22.5,5.661z"
            />
            <path
              style="display: inline; fill: #ffffff"
              d="M33.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C34,5.437,33.776,5.661,33.5,5.661z"
            />
            <path
              style="display: inline; fill: #ffffff"
              d="M44.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C45,5.437,44.776,5.661,44.5,5.661z"
            />
            <path
              style="display: inline; fill: #ffffff"
              d="M55.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C56,5.437,55.776,5.661,55.5,5.661z"
            />
            <path style="display: inline; fill: #333333" d="M2,52h10V0H2C0.895,0,0,0.895,0,2v48C0,51.105,0.895,52,2,52z" />
            <path
              style="display: inline; fill: #ffffff"
              d="M9.5,12h-7C2.224,12,2,11.776,2,11.5v-2C2,9.224,2.224,9,2.5,9h7
		C9.776,9,10,9.224,10,9.5v2C10,11.776,9.776,12,9.5,12z"
            />
            <path
              style="display: inline; fill: #ffffff"
              d="M9.5,18h-7C2.224,18,2,17.776,2,17.5v-2C2,15.224,2.224,15,2.5,15h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,17.776,9.776,18,9.5,18z"
            />
            <path
              style="display: inline; fill: #ffffff"
              d="M9.5,24h-7C2.224,24,2,23.776,2,23.5v-2C2,21.224,2.224,21,2.5,21h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,23.776,9.776,24,9.5,24z"
            />
          </g>
        </svg>

        <svg
          v-else
          width="120.000000"
          height="80.000000"
          viewBox="0 0 82 52"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          @click="onSelect('topMiddleSiderBottom', 'header', true)"
          :style="selectStyle"
        >
          <desc>混合型导航</desc>
          <defs />
          <g>
            <path
              style="fill: #ffffff"
              d="M80,52H2c-1.105,0-2-0.895-2-2V2c0-1.105,0.895-2,2-2h78c1.105,0,2,0.895,2,2v48
		C82,51.105,81.105,52,80,52z"
            />
            <g id="替换色1">
              <path style="fill: var(--w-primary-color)" d="M80,0H2C0.895,0,0,0.895,0,2v6h82V2C82,0.895,81.105,0,80,0z" />
            </g>
            <path
              style="fill: #ffffff"
              d="M9.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C10,5.437,9.776,5.661,9.5,5.661z"
            />
            <path
              style="fill: #ffffff"
              d="M20.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C21,5.437,20.776,5.661,20.5,5.661z"
            />
            <path
              style="fill: #ffffff"
              d="M31.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C32,5.437,31.776,5.661,31.5,5.661z"
            />
            <path
              style="fill: #ffffff"
              d="M42.5,5.661h-7c-0.276,0-0.5-0.224-0.5-0.5v-2c0-0.276,0.224-0.5,0.5-0.5h7
		c0.276,0,0.5,0.224,0.5,0.5v2C43,5.437,42.776,5.661,42.5,5.661z"
            />
            <path style="fill: #333333" d="M2,52h10V8H0v42C0,51.105,0.895,52,2,52z" />
            <path
              style="fill: #ffffff"
              d="M9.5,14h-7C2.224,14,2,13.776,2,13.5v-2C2,11.224,2.224,11,2.5,11h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,13.776,9.776,14,9.5,14z"
            />
            <path
              style="fill: #ffffff"
              d="M9.5,20h-7C2.224,20,2,19.776,2,19.5v-2C2,17.224,2.224,17,2.5,17h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,19.776,9.776,20,9.5,20z"
            />
            <path
              style="fill: #ffffff"
              d="M9.5,26h-7C2.224,26,2,25.776,2,25.5v-2C2,23.224,2.224,23,2.5,23h7c0.276,0,0.5,0.224,0.5,0.5v2
		C10,25.776,9.776,26,9.5,26z"
            />
          </g>
        </svg>
      </template>
    </div>
  </div>
</template>
<style lang="less" scoped>
.menu-layout-select {
  display: flex;
  justify-content: space-between;
  > div {
    padding: 20px 30px;
    background: rgba(0, 0, 0, 0.05);
    border-radius: 4px;
    display: flex;
    align-items: center;
    border: 2px solid transparent;
  }
  .selected {
    border: 2px solid var(--w-primary-color);
  }
}
</style>
<script type="text/babel">
export default {
  name: 'MenuLayoutSelect',
  props: {
    config: Object,
    siderToTop: Boolean,
    selectStyle: Object,
    divStyle: Object
  },
  components: {},
  computed: {},
  data() {
    return {
      siderTop: this.config.layoutType == 'siderTopMiddleBottom'
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSelect(layoutType, menuPosition, appMenusToSider) {
      this.$set(this.config, 'menuPosition', menuPosition);
      this.$set(this.config, 'layoutType', layoutType);
      if (appMenusToSider != undefined) {
        this.$set(this.config, 'appMenusToSider', appMenusToSider);
      }
      // if (menuPosition == 'header' && !appMenusToSider && layoutType == 'topMiddleSiderBottom') {
      //   this.siderTop = true;
      // }
    }
  }
};
</script>
