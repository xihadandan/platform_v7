<template>
  <div class="design-empty-div">
    <div style="margin-bottom: 60px">请先对业务进行阶段划分，以设计业务流程</div>
    <a-row type="flex" class="f_nowrap f_y_c">
      <a-col flex="80px" class="col-name" style="text-align: left">划分阶段</a-col>
      <a-col flex="280px">
        <div class="flex select-div f_y_c f_x_s">
          <div
            v-for="(item, index) in nodesCount"
            :key="'count_' + index"
            :class="[count == item.count ? 'selected' : '']"
            @click="selectCount(item.count)"
          >
            <div class="icon">
              <template v-if="item.id == 'three'">
                <svg x="0px" y="0px" viewBox="0 0 64 32" style="enable-background: new 0 0 64 32" xml:space="preserve">
                  <g>
                    <path
                      style="fill: #ffffff"
                      d="M60,32H4c-2.209,0-4-1.791-4-4V4c0-2.209,1.791-4,4-4h56c2.209,0,4,1.791,4,4v24
		C64,30.209,62.209,32,60,32z"
                    />
                  </g>
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M6.5,7h13C19.776,7,20,7.224,20,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-13C6.224,25,6,24.776,6,24.5v-17
	C6,7.224,6.224,7,6.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M25.5,7h13C38.776,7,39,7.224,39,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-13c-0.276,0-0.5-0.224-0.5-0.5
	v-17C25,7.224,25.224,7,25.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M44.5,7h13C57.776,7,58,7.224,58,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-13c-0.276,0-0.5-0.224-0.5-0.5
	v-17C44,7.224,44.224,7,44.5,7z"
                  />
                </svg>
              </template>
              <template v-else-if="item.id == 'four'">
                <svg x="0px" y="0px" viewBox="0 0 64 32" style="enable-background: new 0 0 64 32" xml:space="preserve">
                  <g>
                    <path
                      style="fill: #ffffff"
                      d="M60,32H4c-2.209,0-4-1.791-4-4V4c0-2.209,1.791-4,4-4h56c2.209,0,4,1.791,4,4v24
		C64,30.209,62.209,32,60,32z"
                    />
                  </g>
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M6.5,7h9C15.776,7,16,7.224,16,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-9C6.224,25,6,24.776,6,24.5v-17
	C6,7.224,6.224,7,6.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M20.5,7h9C29.776,7,30,7.224,30,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-9c-0.276,0-0.5-0.224-0.5-0.5v-17
	C20,7.224,20.224,7,20.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M34.5,7h9C43.776,7,44,7.224,44,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-9c-0.276,0-0.5-0.224-0.5-0.5v-17
	C34,7.224,34.224,7,34.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M48.5,7h9C57.776,7,58,7.224,58,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-9c-0.276,0-0.5-0.224-0.5-0.5v-17
	C48,7.224,48.224,7,48.5,7z"
                  />
                </svg>
              </template>
              <template v-else-if="item.id == 'five'">
                <svg x="0px" y="0px" viewBox="0 0 64 32" style="enable-background: new 0 0 64 32" xml:space="preserve">
                  <g>
                    <path
                      style="fill: #ffffff"
                      d="M60,32H4c-2.209,0-4-1.791-4-4V4c0-2.209,1.791-4,4-4h56c2.209,0,4,1.791,4,4v24
		C64,30.209,62.209,32,60,32z"
                    />
                  </g>
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M7.5,7h6C13.776,7,14,7.224,14,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-6C7.224,25,7,24.776,7,24.5v-17
	C7,7.224,7.224,7,7.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M18.5,7h6C24.776,7,25,7.224,25,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-6c-0.276,0-0.5-0.224-0.5-0.5v-17
	C18,7.224,18.224,7,18.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M29.5,7h6C35.776,7,36,7.224,36,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-6c-0.276,0-0.5-0.224-0.5-0.5v-17
	C29,7.224,29.224,7,29.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M40.5,7h6C46.776,7,47,7.224,47,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-6c-0.276,0-0.5-0.224-0.5-0.5v-17
	C40,7.224,40.224,7,40.5,7z"
                  />
                  <path
                    style="fill: var(--w-design-empty-icon-fill-color)"
                    d="M51.5,7h6C57.776,7,58,7.224,58,7.5v17c0,0.276-0.224,0.5-0.5,0.5h-6c-0.276,0-0.5-0.224-0.5-0.5v-17
	C51,7.224,51.224,7,51.5,7z"
                  />
                </svg>
              </template>
            </div>
            <div class="name">{{ item.name }}</div>
          </div>
          <div>
            <a-input-number v-model="count" :min="1" style="width: 64px" />
            <div class="name">其他</div>
          </div>
        </div>
      </a-col>
      <a-col flex="100px" class="col-name" style="text-align: right; padding-right: 20px">布局</a-col>
      <a-col flex="200px">
        <div class="flex select-div f_y_c f_x_s">
          <div
            v-for="(item, index) in designlayout"
            :key="'layout_' + index"
            :class="[layout == item.id ? 'selected' : '']"
            @click="selectLayout(item.id)"
          >
            <div class="icon-layout">
              <template v-if="item.id == 'horizontal'">
                <svg x="0px" y="0px" viewBox="0 0 112 64" style="enable-background: new 0 0 112 64" xml:space="preserve">
                  <g>
                    <path
                      style="fill: #ffffff"
                      d="M108,64H4c-2.209,0-4-1.791-4-4V4c0-2.209,1.791-4,4-4h104c2.209,0,4,1.791,4,4v56
		C112,62.209,110.209,64,108,64z"
                    />
                  </g>
                  <g id="th">
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M9,10c0-0.552,0.448-1,1-1h27.666c0.302,0,0.587,0.136,0.777,0.371l4.048,5
		c0.297,0.367,0.297,0.892,0,1.258l-4.048,5C38.253,20.864,37.967,21,37.666,21H10c-0.552,0-1-0.448-1-1V10z"
                    />
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M42.367,10.64C41.824,9.989,42.287,9,43.135,9h27.397c0.297,0,0.578,0.132,0.768,0.36l4.167,5
		c0.309,0.371,0.309,0.91,0,1.28l-4.167,5C71.11,20.868,70.828,21,70.532,21H43.135c-0.848,0-1.311-0.989-0.768-1.64l3.1-3.72
		c0.309-0.371,0.309-0.91,0-1.28L42.367,10.64z"
                    />
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M102,9H76.09c-0.84,0-1.306,0.973-0.779,1.627l3.017,3.745c0.295,0.366,0.295,0.888,0,1.255
		l-3.017,3.745C74.784,20.027,75.25,21,76.09,21H102c0.552,0,1-0.448,1-1V10C103,9.448,102.552,9,102,9z"
                    />
                  </g>
                  <g>
                    <path
                      style="fill: #f2f2f2"
                      d="M99.857,55h-2v-1h2V55z M95.857,55h-2v-1h2V55z M91.857,55h-2v-1h2V55z M87.857,55h-2v-1h2V55z
		 M83.857,55h-2v-1h2V55z M79.857,55h-2v-1h2V55z M75.857,55h-2v-1h2V55z M71.857,55h-2v-1h2V55z M67.857,55h-2v-1h2V55z M63.857,55
		h-2v-1h2V55z M59.857,55h-2v-1h2V55z M55.857,55h-2v-1h2V55z M51.857,55h-2v-1h2V55z M47.857,55h-2v-1h2V55z M43.857,55h-2v-1h2V55
		z M39.857,55h-2v-1h2V55z M35.857,55h-2v-1h2V55z M31.857,55h-2v-1h2V55z M27.857,55h-2v-1h2V55z M23.857,55h-2v-1h2V55z
		 M19.857,55h-2v-1h2V55z M15.857,55h-2v-1h2V55z M11.83,54.995c-0.815-0.046-1.56-0.411-2.097-1.029l0.755-0.656
		c0.358,0.413,0.854,0.657,1.397,0.688L11.83,54.995z M102.03,54.21l-0.678-0.736c0.402-0.369,0.632-0.873,0.646-1.416l1,0.027
		C102.976,52.901,102.632,53.655,102.03,54.21z M10,51.786H9v-2h1V51.786z M103,50.071h-1v-2h1V50.071z M10,47.786H9v-2h1V47.786z
		 M103,46.071h-1v-2h1V46.071z M10,43.786H9v-2h1V43.786z M103,42.071h-1v-2h1V42.071z M10,39.786H9v-2h1V39.786z M103,38.071h-1v-2
		h1V38.071z M10,35.786H9v-2h1V35.786z M103,34.071h-1v-2h1V34.071z M10,31.786H9v-2h1V31.786z M103,30.071h-1v-2h1V30.071z
		 M10.007,27.828l-0.996-0.084c0.067-0.801,0.465-1.555,1.089-2.066l0.634,0.773C10.312,26.798,10.053,27.287,10.007,27.828z
		 M101.433,26.605C101.055,26.215,100.546,26,100,26v-1c0.818,0,1.582,0.323,2.151,0.909L101.433,26.605z M98,26h-2v-1h2V26z M94,26
		h-2v-1h2V26z M90,26h-2v-1h2V26z M86,26h-2v-1h2V26z M82,26h-2v-1h2V26z M78,26h-2v-1h2V26z M74,26h-2v-1h2V26z M70,26h-2v-1h2V26z
		 M66,26h-2v-1h2V26z M62,26h-2v-1h2V26z M58,26h-2v-1h2V26z M54,26h-2v-1h2V26z M50,26h-2v-1h2V26z M46,26h-2v-1h2V26z M42,26h-2
		v-1h2V26z M38,26h-2v-1h2V26z M34,26h-2v-1h2V26z M30,26h-2v-1h2V26z M26,26h-2v-1h2V26z M22,26h-2v-1h2V26z M18,26h-2v-1h2V26z
		 M14,26h-2v-1h2V26z"
                    />
                  </g>
                  <path
                    style="fill: #f2f2f2"
                    d="M16,30h21c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2H16c-1.105,0-2-0.895-2-2v-4
	C14,30.895,14.895,30,16,30z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M16,42h21c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2H16c-1.105,0-2-0.895-2-2v-4
	C14,42.895,14.895,42,16,42z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M45,30h22c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2H45c-1.105,0-2-0.895-2-2v-4
	C43,30.895,43.895,30,45,30z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M45,42h22c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2H45c-1.105,0-2-0.895-2-2v-4
	C43,42.895,43.895,42,45,42z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M75,30h21c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2H75c-1.105,0-2-0.895-2-2v-4
	C73,30.895,73.895,30,75,30z"
                  />
                </svg>
              </template>
              <template v-else-if="item.id == 'vertical'">
                <svg x="0px" y="0px" viewBox="0 0 112 64" style="enable-background: new 0 0 112 64" xml:space="preserve">
                  <g>
                    <path
                      style="fill: #ffffff"
                      d="M108,64H4c-2.209,0-4-1.791-4-4V4c0-2.209,1.791-4,4-4h104c2.209,0,4,1.791,4,4v56
		C112,62.209,110.209,64,108,64z"
                    />
                  </g>
                  <g id="th">
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M19.855,8.516c0.552,0,1,0.448,1,1v10.666c0,0.302-0.136,0.587-0.371,0.777l-5,4.048
		c-0.367,0.297-0.892,0.297-1.258,0l-5-4.048c-0.235-0.19-0.371-0.476-0.371-0.777l0-10.666c0-0.552,0.448-1,1-1L19.855,8.516z"
                    />
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M19.215,23.882c0.651-0.543,1.64-0.08,1.64,0.768v11.397c0,0.297-0.132,0.578-0.36,0.768l-5,4.167
		c-0.371,0.309-0.91,0.309-1.28,0l-5-4.167c-0.228-0.19-0.36-0.471-0.36-0.768l0-11.397c0-0.848,0.989-1.311,1.64-0.768l3.72,3.1
		c0.371,0.309,0.91,0.309,1.28,0L19.215,23.882z"
                    />
                    <path
                      style="fill: var(--w-primary-color)"
                      d="M20.855,53.516v-12.91c0-0.84-0.973-1.306-1.627-0.779l-3.745,3.017
		c-0.366,0.295-0.888,0.295-1.255,0l-3.745-3.017c-0.654-0.527-1.627-0.061-1.627,0.779v5.41l0,7.5c0,0.552,0.448,1,1,1h10
		C20.407,54.516,20.855,54.068,20.855,53.516z"
                    />
                  </g>
                  <g>
                    <path
                      style="fill: #f2f2f2"
                      d="M99.713,54.516h-2v-1h2V54.516z M95.713,54.516h-2v-1h2V54.516z M91.713,54.516h-2v-1h2V54.516z
		 M87.713,54.516h-2v-1h2V54.516z M83.713,54.516h-2v-1h2V54.516z M79.713,54.516h-2v-1h2V54.516z M75.713,54.516h-2v-1h2V54.516z
		 M71.713,54.516h-2v-1h2V54.516z M67.713,54.516h-2v-1h2V54.516z M63.713,54.516h-2v-1h2V54.516z M59.713,54.516h-2v-1h2V54.516z
		 M55.713,54.516h-2v-1h2V54.516z M51.713,54.516h-2v-1h2V54.516z M47.713,54.516h-2v-1h2V54.516z M43.713,54.516h-2v-1h2V54.516z
		 M39.713,54.516h-2v-1h2V54.516z M35.713,54.516h-2v-1h2V54.516z M31.713,54.516h-2v-1h2V54.516z M27.685,54.511
		c-0.815-0.046-1.56-0.411-2.097-1.029l0.755-0.656c0.358,0.413,0.854,0.657,1.397,0.688L27.685,54.511z M101.884,53.726
		l-0.676-0.736c0.401-0.369,0.632-0.872,0.646-1.416l1,0.027C102.831,52.417,102.487,53.172,101.884,53.726z M25.855,51.302h-1v-2h1
		V51.302z M102.855,49.587h-1v-2h1V49.587z M25.855,47.302h-1v-2h1V47.302z M102.855,45.587h-1v-2h1V45.587z M25.855,43.302h-1v-2h1
		V43.302z M102.855,41.587h-1v-2h1V41.587z M25.855,39.302h-1v-2h1V39.302z M102.855,37.587h-1v-2h1V37.587z M25.855,35.302h-1v-2h1
		V35.302z M102.855,33.587h-1v-2h1V33.587z M25.855,31.302h-1v-2h1V31.302z M102.855,29.587h-1v-2h1V29.587z M25.855,27.302h-1v-2h1
		V27.302z M102.855,25.587h-1v-2h1V25.587z M25.855,23.302h-1v-2h1V23.302z M102.855,21.587h-1v-2h1V21.587z M25.855,19.302h-1v-2h1
		V19.302z M102.855,17.587h-1v-2h1V17.587z M25.855,15.302h-1v-2h1V15.302z M102.855,13.587h-1v-2h1V13.587z M25.862,11.344
		l-0.996-0.084c0.067-0.802,0.465-1.556,1.089-2.066l0.634,0.773C26.167,10.313,25.908,10.802,25.862,11.344z M101.29,10.122
		c-0.38-0.392-0.889-0.606-1.435-0.606v-1c0.818,0,1.582,0.323,2.151,0.909L101.29,10.122z M97.855,9.516h-2v-1h2V9.516z
		 M93.855,9.516h-2v-1h2V9.516z M89.855,9.516h-2v-1h2V9.516z M85.855,9.516h-2v-1h2V9.516z M81.855,9.516h-2v-1h2V9.516z
		 M77.855,9.516h-2v-1h2V9.516z M73.855,9.516h-2v-1h2V9.516z M69.855,9.516h-2v-1h2V9.516z M65.855,9.516h-2v-1h2V9.516z
		 M61.855,9.516h-2v-1h2V9.516z M57.855,9.516h-2v-1h2V9.516z M53.855,9.516h-2v-1h2V9.516z M49.855,9.516h-2v-1h2V9.516z
		 M45.855,9.516h-2v-1h2V9.516z M41.855,9.516h-2v-1h2V9.516z M37.855,9.516h-2v-1h2V9.516z M33.855,9.516h-2v-1h2V9.516z
		 M29.855,9.516h-2v-1h2V9.516z"
                    />
                  </g>
                  <path
                    style="fill: #f2f2f2"
                    d="M31.855,15.516h16c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-16c-1.105,0-2-0.895-2-2v-4
	C29.855,16.411,30.75,15.516,31.855,15.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M31.855,39.516h16c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-16c-1.105,0-2-0.895-2-2v-4
	C29.855,40.411,30.75,39.516,31.855,39.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M31.855,27.516h16c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-16c-1.105,0-2-0.895-2-2v-4
	C29.855,28.411,30.75,27.516,31.855,27.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M54.855,15.516h18c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-18c-1.105,0-2-0.895-2-2v-4
	C52.855,16.411,53.75,15.516,54.855,15.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M54.855,39.516h18c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-18c-1.105,0-2-0.895-2-2v-4
	C52.855,40.411,53.75,39.516,54.855,39.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M54.855,27.516h18c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-18c-1.105,0-2-0.895-2-2v-4
	C52.855,28.411,53.75,27.516,54.855,27.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M79.855,15.516h16c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-16c-1.105,0-2-0.895-2-2v-4
	C77.855,16.411,78.75,15.516,79.855,15.516z"
                  />
                  <path
                    style="fill: #f2f2f2"
                    d="M79.855,39.516h16c1.105,0,2,0.895,2,2v4c0,1.105-0.895,2-2,2h-16c-1.105,0-2-0.895-2-2v-4
	C77.855,40.411,78.75,39.516,79.855,39.516z"
                  />
                </svg>
              </template>
            </div>
            <div class="name">{{ item.name }}</div>
          </div>
        </div>
      </a-col>
    </a-row>
    <div style="margin-top: 80px">
      <a-button type="primary" @click="onDesignNodeClick">
        开始设计
        <Icon></Icon>
      </a-button>
    </div>
  </div>
</template>
<script type="text/babel">
import { createNodes } from '../designer/utils';
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'ProcessBuildEmpty',
  props: {
    designer: Object
  },
  inject: ['pageContext'],
  components: {},
  computed: {},
  data() {
    return {
      nodesCount: [
        {
          name: '三阶段',
          id: 'three',
          count: '3'
        },
        {
          name: '四阶段',
          id: 'four',
          count: '4'
        },
        {
          name: '五阶段',
          id: 'five',
          count: '5'
        }
      ],
      designlayout: [
        {
          name: '横向布局',
          id: 'horizontal'
        },
        {
          name: '竖向布局',
          id: 'vertical'
        }
      ],
      count: 3,
      layout: 'horizontal'
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    selectCount(count) {
      this.count = count;
    },
    selectLayout(layout) {
      this.layout = layout;
    },
    onDesignNodeClick() {
      const _this = this;
      if (_this.designer.graph.getCellCount() > 0) {
        _this.$message.warning('布局已经存在，不能再次添加！');
        return;
      }
      if (_this.count) {
        let nodes = createNodes(_this.count);
        let designNode = {
          count: _this.count,
          layout: _this.layout,
          name: _this.count + '阶段'
        };
        let nodeTemplate = Object.assign({ id: 'group_' + generateId(), nodes }, designNode);
        _this.designer.initFromNodeTemplate(nodeTemplate);
        _this.pageContext.emitEvent(`processConfigurationPanel:processConfigRef`, { collapse: false });
        _this.pageContext.emitEvent(`processSelectPanel`, { collapse: false });
        _this.designer.graph.translate(0, 0); // 画布移到初始位置
      } else {
        _this.$message.warning('请选择阶段！');
        return;
      }
    }
  }
};
</script>
<style lang="less" scoped>
.design-empty-div {
  --w-design-empty-icon-fill-color: #e5e5e5;
  .select-div {
    > div {
      cursor: pointer;

      &.selected {
        --w-design-empty-icon-fill-color: var(--w-primary-color);
      }
      .icon {
        border-radius: 4px;
        border: 1px solid var(--w-design-empty-icon-fill-color);
        width: 56px;
        height: 29px;
      }
      .icon-layout {
        border-radius: 4px;
        border: 1px solid var(--w-design-empty-icon-fill-color);
        width: 90px;
        height: 52px;
      }
      .name {
        font-size: var(--w-font-size-base);
        color: var(--w-text-color-dark);
      }
    }
  }
  .col-name {
    font-size: var(--w-font-size-base);
    color: var(--w-text-color-darker);
    margin-top: -20px;
  }
}
</style>
