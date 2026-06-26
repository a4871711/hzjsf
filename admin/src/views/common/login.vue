<template>
	<div>
		<section id="wrapper">
			<div class="login-main">
				<div id="logo">
					<p class="shadow"></p>
					<div class="logo">
						<img src="~@/assets/img/logo.svg" alt />
						<p class="zn">矢历运动管理系统</p>
						<p class="en"></p>
					</div>
				</div>
				<div id="login">
					<h3 class="login-title">管理员登录</h3>
					<el-form class="form" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" status-icon>
						<el-form-item prop="userName"><el-input v-model="dataForm.userName" placeholder="帐号"></el-input></el-form-item>
						<el-form-item prop="password"><el-input v-model="dataForm.password" type="password" placeholder="密码"></el-input></el-form-item>
						<el-form-item prop="captcha">
							<el-row :gutter="20">
								<el-col :span="14"><el-input v-model="dataForm.captcha" placeholder="验证码"></el-input></el-col>
								<el-col :span="10" class="login-captcha"><img style="height:38px;" :src="captchaPath" @click="getCaptcha()" alt="" /></el-col>
							</el-row>
						</el-form-item>
						<el-form-item><el-button class="login-btn-submit" type="primary" @click="dataFormSubmit()">登录</el-button></el-form-item>
					</el-form>
				</div>
			</div>
		</section>
	</div>
</template>

<script>
import { getUUID } from '@/utils'
import { mapMutations } from 'vuex'
export default {
	data() {
		return {
			dataForm: {
				userName: '',
				password: '',
				uuid: '',
				//captcha: ''
			},
			dataRule: {
				userName: [{ required: true, message: '帐号不能为空', trigger: 'blur' }],
				password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
				captcha: [{ required: true, message: '验证码不能为空', trigger: 'blur' }]
			},
			captchaPath: (process.env.NODE_ENV !== 'production' ? 'proxyApi/' : '/') + 'captcha.jpg?t=' + Math.random()
		}
	},
	created() {
		//this.getCaptcha()
	},
	mounted() {},
	methods: {
		// 提交表单
		dataFormSubmit() {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.login()
				}
			})
		},
		async login() {
			var res = await this.apis.login({
				username: this.dataForm.userName,
				password: this.dataForm.password,
				captcha: this.dataForm.captcha
			}).catch(e => {
				this.getCaptcha()
			})
			
			if(!res)return


			const info = await this.apis.getInfo()

			localStorage.role = info.user.userId
			localStorage.token = info.user.userId

			this.$router.push({
				path: '/home'
			})
		},
		...mapMutations({
			setUserId: 'user/updateId'
		}),
		// 获取验证码
		async getCaptcha() {
			this.captchaPath = (process.env.NODE_ENV !== 'production' ? 'proxyApi/' : '/') + 'captcha.jpg?t=' + Math.random()
		}
	}
}
</script>

<style scoped lang="scss">
#wrapper {
	width: 100vw;
	height: 100vh;
	background-image: url(~@/assets/img/login_bg.png);
}
.login-main {
	width: 80vw;
	max-width: 840px;
	height: 450px;
	position: relative;
	margin:0 auto;
	top: 50px;
}
#logo {
	width: 80vw;
	height: 250px;
	position: relative;
	.shadow {
		position: absolute;
		width: 80vw;
		height: 250px;
		background-color: rgba(0, 121, 254, 0.8);
		left: 0px;
		top: 0px;
	}
	.logo {
		position: absolute;
		width: 80vw;
		height: 250px;
		z-index: 1;
		font-size: 26px;
		font-weight: 700;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		color: #fff;
		img {
			width: 115px;
			height: 115px;
		}
		.zn {
			margin-top: 30px;
		}
		.en {
			margin-top: 6px;
			font-size: 20px;
			font-weight: 410;
		}
	}
}
#login {
	background: #fff;
	width: 80vw;
	min-height: 300px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	.form {
		width: 68vw;
	}
	.login-main {
		position: absolute;
		top: 0;
		right: 0;
		padding: 150px 60px 180px;
		width: 470px;
		min-height: 100%;
		background-color: #fff;
	}
	.login-title {
		font-size: 20px;
		color: #666;
		margin: 20px;
	}
	.login-captcha {
		overflow: hidden;
		> img {
			width: 100%;
			cursor: pointer;
		}
	}
	.login-btn-submit {
		width: 100%;
		margin-top: 20px;
	}
}
@media only screen and (min-width: 840px) {
	.login-main {	
		width: 840px;
		display: flex;
		top:100px;
	}
	#logo, #login {
		width: 420px;
		height: 450px;		
		.shadow, .logo {
			width: 420px;
			height: 450px;	
		}
		.form {
			width: 350px;
		}
	}
}
</style>
