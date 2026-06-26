<template>
	<div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from '../mixins/resize'

export default {
	name: 'LineChart',
	mixins: [resize],
	props: {
		className: {
			type: String,
			default: 'chart'
		},
		width: {
			type: String,
			default: '100%'
		},
		height: {
			type: String,
			default: '350px'
		},
		autoResize: {
			type: Boolean,
			default: true
		},
		chartData: {
			type: Object,
			required: true
		}
	},
	data() {
		return {
			chart: null
		}
	},
	watch: {
		chartData: {
			deep: true,
			handler(val) {
				this.setOptions(val)
			}
		}
	},
	mounted() {
		this.$nextTick(() => {
			this.initChart()
		})
	},
	beforeDestroy() {
		if (!this.chart) {
			return
		}
		this.chart.dispose()
		this.chart = null
	},
	methods: {
		initChart() {
			this.chart = echarts.init(this.$el, 'macarons')
			this.setOptions(this.chartData)
		},
		setOptions({ xData, yData, name, title, isShowSolid } = {}) {
			this.chart.setOption({
				title: {
					text: title,
					textStyle: {
						color: '#333'
					},

					left: '40px'
					// top: 'auto' ,
					// right: 'auto' ,
					// bottom: 'auto'
				},
				
				grid: {
					left: 10,
					right: 10,
					bottom: 30,
					top: 30,
					containLabel: true
				},
				tooltip: {
					trigger: 'axis',
					/* axisPointer: {
						type: 'cross'
					}, */
					padding: [5, 10]
				},
				xAxis: {
					data: xData,
					axisLabel: {
						rotate: isShowSolid ? 45 : 0
					},
					axisLine: {
						lineStyle: {
							color: '#333'
						}
					},
					boundaryGap: true
				},
				yAxis: {
					type: 'value',
					axisLine: {
						lineStyle: {
							color: '#333'
						}
					},
				},
				dataZoom: [
					{
						type: 'inside',
						show: true,
						xAxisIndex: [0],
						start: 1,
						end: 100
					},
					{
						type: 'slider',
						show: isShowSolid,
						xAxisIndex: [0],
						handleSize: 20, //滑动条的 左右2个滑动条的大小
						height: 8, //组件高度
						left: 30, //左边的距离
						right: 40, //右边的距离
						bottom: 10, //右边的距离
						handleColor: '#ddd', //h滑动图标的颜色
						handleStyle: {
							borderColor: '#cacaca',
							borderWidth: '1',
							shadowBlur: 2,
							background: '#ddd',
							shadowColor: '#ddd'
						},
						fillerColor: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
							{
								//给颜色设置渐变色 前面4个参数，给第一个设置1，第四个设置0 ，就是水平渐变
								//给第一个设置0，第四个设置1，就是垂直渐变
								offset: 0,
								color: '#1eb5e5'
							},
							{
								offset: 1,
								color: '#5ccbb1'
							}
						]),
						backgroundColor: '#ddd', //两边未选中的滑动条区域的颜色
						showDataShadow: false, //是否显示数据阴影 默认auto
						showDetail: false, //即拖拽时候是否显示详细数值信息 默认true
						handleIcon:
							'M-292,322.2c-3.2,0-6.4-0.6-9.3-1.9c-2.9-1.2-5.4-2.9-7.6-5.1s-3.9-4.8-5.1-7.6c-1.3-3-1.9-6.1-1.9-9.3c0-3.2,0.6-6.4,1.9-9.3c1.2-2.9,2.9-5.4,5.1-7.6s4.8-3.9,7.6-5.1c3-1.3,6.1-1.9,9.3-1.9c3.2,0,6.4,0.6,9.3,1.9c2.9,1.2,5.4,2.9,7.6,5.1s3.9,4.8,5.1,7.6c1.3,3,1.9,6.1,1.9,9.3c0,3.2-0.6,6.4-1.9,9.3c-1.2,2.9-2.9,5.4-5.1,7.6s-4.8,3.9-7.6,5.1C-285.6,321.5-288.8,322.2-292,322.2z',
						filterMode: 'filter'
					}
				],
				series: [
					{
						name: name,
						type: 'line',
						data: yData
					}
				]
			})
		}
	}
}
</script>
