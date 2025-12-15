<template>
  <svg
    version="1.1"
    id="图层_1"
    xmlns="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    x="0px"
    y="0px"
    :viewBox="viewBox"
    :style="svgStyle"
    xml:space="preserve"
  >
    <g id="bg">
      <g style="opacity: 0.4">
        <rect style="fill: transparent" :width="vWidth" :height="vHeight" />
      </g>
    </g>
    <g id="tr">
      <path style="fill: #2693ff" :d="'M' + vWidth + ',0v8h-2V2h-6V0H120z'" />
    </g>
    <g id="tl">
      <path style="fill: #2693ff" d="M0,0h8v2H2v6H0V0z" />
    </g>
    <g id="bl">
      <path style="fill: #2693ff" :d="'M0,' + vHeight + 'v-8h2v6h6v2H0z'" />
    </g>
    <g id="br">
      <path style="fill: #2693ff" :d="'M' + vWidth + ',' + vHeight + 'h-8v-2h6v-6h2V80z'" />
    </g>
    <g id="titlebg">
      <linearGradient
        id="SVGID_1_"
        gradientUnits="userSpaceOnUse"
        x1="2"
        y1="-6"
        :x2="vWidth - 2"
        y2="-6"
        gradientTransform="matrix(1 0 0 -1 0 2)"
      >
        <stop offset="0" style="stop-color: #ff5555; stop-opacity: 0.8" />
        <stop offset="1" style="stop-color: #ff5555; stop-opacity: 0" />
      </linearGradient>
      <rect x="2" y="2" style="opacity: 0.6; fill: url(#SVGID_1_); enable-background: new" :width="vWidth - 4" height="12" />
    </g>
    <g id="a1">
      <rect id="_x31_" x="5" y="5" style="fill: #f9db00" width="2" height="6" />
    </g>
    <g id="a2">
      <rect x="0" :y="vHeight - 2" style="opacity: 0; fill: #f9db00" :width="vWidth" height="2" />

      <image
        style="overflow: visible"
        :width="vWidth"
        height="4"
        xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAAECAMAAAByWgtaAAAACGFjVEwAAAAVAAAAACFN2OIAAAL6
UExURQAAAANi/wNh/wNh/wRh/wVh/wVh/wVh/wRi/wVj/wRj/wVl/wRl/wVl/wVh/wRw/wVn/wdm
/wZi/wV4/wZi/wdj/wZ8/wZi/wdj/wZj/wd//wdk/wpj/wiB/wVi/wRs/wyO/wdi/wRu/wdi/wRs
/xOe/wiB/whl/wRi/whn/wZ6/yG4/whk/5Pw/wVl/wmE/1HQ/wVm/wZm/wVz/wuM/wVl/wVm/wNr
/wZj/w+U/wZ7/wVn/xGb/wVo/xap/wZj/wNq/wRy/wZ8/wVl/yW8/wVl/wRu/3Hd/z7P/wVm/wd8
/5ft/wRu/wNc/wVn/wqO/wNs/wRx/wRn/wRw/wiC/wNn/wRx/wNo/wRz/w+V/wNr/weB/wNp/wRz
/wNq/wRg/wR0/wNq/wV5/wyP/xmt/wNr/wV2/yW//wNs/wV2/wZ+/1fh/633/wRu/wV3/wmG/wRv
/zvG/wZ7/2zU/wRw/wRx/wZ6/wiH/wRx/wZ8/w2S/waF/wRy/wd9/wRz/wRz/wiI/w+U/weA/wR0
/xOg/wZ5/wV1/wh//weH/wV3/xuv/xGa/wV3/wh//wiI/0TO/wV5/4De/wmG/yK8/wZ7/xWm/0va
/1je/wmL/wd9/53w/zzU/wZ9/wqN/wuF/3Po/673/yu8/wd9/wqO/wqE/wZ//1HM/wuS/wyI/2rT
/waA/wyY/w2N/weB/w+R/xCc/weC/w+T/weD/xWj/xCY/wiD/xir/xOf/wiE/xu1/wmF/xWn/yK/
/xmt/zHK/wmD/zzT/wmI/yC0/0Xa/6z2/2Hk/6/3/07f/37r/47v/6Dz/3Ho/ya6/wqI/y/B/1jU
/zjH/33d/wuK/0zL/13P/0DV/wuM/2zj/2zU/4Xo/5Ls/6Hw/6Lw/wyO/w2R/w6S/xCX/xCY/xGc
/xOg/xWm/xuy/xep/ye8/xqw/zPG/xy3/0DO/0/S/yG9/1vU/2XX/yrE/3Ha/4Hf/3vd/zHJ/znP
/0LW/1Dc/1/g/2zj/6Hw/3zn/4vr/5ju/wAyby0AAAD+dFJOUwACAwUHCAkLDA4PERIUFRUXGBkZ
Gx0dHh8gICEiIiMjJCUlJycnKCkqKysrLCwtLS4vMDAwMTIzMzM0NTU3ODk5OTo7Oz09PT0/Pz9A
QEFBQkNERUVGR0lKSktLTU1PT1BRUVJTVFRWV1dYWFhZWltcXF1eX2FhYWNkZGRlZ2dpamtsbGxu
cHFyc3N0dnd3eHp6e3t9fn5+f4CAgoODg4SEhoeHiYuLjI2Oj5CRk5SXmJmcnZ6foaOjpqioq6yt
rq6vr6+wsLCwsLCwsLO0tLW3uLm6u7u8vL29vb29wcXKzdHV2t7i5Ofp7O7w8fLy8/T09fX29/r7
+/v8/Pz8nCiVygAAABpmY1RMAAAAAAAAAHgAAAAEAAAAAAAAAAAAAQAeAADUWrkcAAAADklEQVQY
02NgGAXDHQAAAeQAASxvYfUAAAAaZmNUTAAAAAEAAABCAAAABAAAAAIAAAAAAAEAHgEARo2NIgAA
AIlmZEFUAAAAAhjTXY9BDsIwEAPHu02Q+P9bi8gmMQdaDsiSb2NrhAJJIARISCiUySNbbwuyRq33
YnkbGxvusn0gQpK4EkQojsx+PFvrRzI1R9U5X7xZLLONLRu2N87v783LyAgLK8gtNmtUnXPM+l8w
GOsSkYUUrLhF1DNbp0Exasztn8jN2rb9AUQ/X9opFSMQAAAAGmZjVEwAAAADAAAAQwAAAAQAAAAE
AAAAAAABAB4BAJRMFNAAAAClZmRBVAAAAAQY03WPQXKDMBAEZ1YCAS7//6GJZUCanRw4p/rcXdUE
owSDJIJgMKKsqHVv23Ysr1IJT33Qf857jDs1JQg20khnZlaQEaU+ctQosSyNbTtae2/HvpcKze/d
P7/n1c9xzzFSKcmCJdEsIBkMmoDXNU3DMBwgI2zNcf2TyIcKO4kRIIKqqSW4YinPy8F3IPvso5/n
9ZWGLBlpI2FnpvEH08uFggL3QlAAAAAaZmNUTAAAAAUAAABCAAAABAAAAAgAAAAAAAEAHgEApH50
NQAAAMJmZEFUAAAABhjTLcxJUsMwEADAWTSS4+AL/P+DcEjFONEyGxf6AY1UmKkQMjIhC0kTlq3t
bTv2fdsKLhwdH69rTH/pUlcN9zDPyDA394JcpN5IUBozcd1ERNrR9vvnUetXK6n9Mcfzec3eu0/V
tTyWuYbpnADBRMRETJkZB1sSAAADJtyQYFYz/1nrfZ599D6mj2n/A5hZhEfJcA1HBkYCfJfKVIvc
mxxnu+8f3wzjer/0+p1jLl1qOQKWh2eGh3rkH0xcjg2+zCe+AAAAGmZjVEwAAAAHAAAAQwAAAAQA
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"
        :transform="'matrix(1 0 0 0.5 0 ' + (vHeight - 2) + ')'"
      ></image>
    </g>
  </svg>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'BorderStyle2',
  props: {
    widget: Object
  },
  components: {},
  computed: {
    svgStyle() {
      return 'enable-background: new ' + this.viewBox;
    },
    viewBox() {
      return [0, 0, this.vWidth, this.vHeight].join(' ');
    },
    vWidth() {
      return this.widget.configuration.style.width;
    },
    vHeight() {
      return this.widget.configuration.style.height;
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
