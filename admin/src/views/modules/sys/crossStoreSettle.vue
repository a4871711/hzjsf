<template>
  <div class="cs-wrap" v-loading="loading">
    <el-alert
      title="会员购买门店 ≠ 实际上课门店 → 按跨店约课处理。本规则仅用于口径配置，本期不参与自动结算。"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom:20px">
    </el-alert>

    <el-form :model="form" label-width="150px" size="small" style="max-width:640px">
      <el-form-item label="规则名称" required>
        <el-input v-model="form.ruleName" style="width:320px" placeholder="请输入规则名称"></el-input>
      </el-form-item>

      <el-form-item label="启用跨店结算">
        <el-switch v-model="form.crossStoreEnabled" :active-value="1" :inactive-value="0"></el-switch>
      </el-form-item>

      <el-form-item label="收入归属方式">
        <el-radio-group v-model="form.incomeOwnerType" @change="onOwnerTypeChange">
          <el-radio :label="1">购买门店</el-radio>
          <el-radio :label="2">上课门店</el-radio>
          <el-radio :label="3">按比例分成</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="分成比例" v-if="Number(form.incomeOwnerType) === 3">
        <div class="cs-ratio">
          <span class="cs-ratio-label">购买门店</span>
          <el-input-number v-model="form.buyStoreRatio" :min="0" :max="100" @change="onRatioChange"></el-input-number>
          <span class="cs-unit">%</span>
          <span class="cs-ratio-label" style="margin-left:20px">上课门店</span>
          <el-input-number v-model="form.lessonStoreRatio" :min="0" :max="100" @change="onRatioChange"></el-input-number>
          <span class="cs-unit">%</span>
          <el-tag :type="ratioSum === 100 ? 'success' : 'danger'" size="medium" style="margin-left:16px">
            合计 {{ ratioSum }}%
          </el-tag>
        </div>
        <div class="cs-tip">按比例分成时，购买门店% + 上课门店% 必须等于 100%</div>
      </el-form-item>

      <el-form-item label="教练课时费归属">
        <el-tag type="warning" size="medium">归授课教练（固定）</el-tag>
      </el-form-item>

      <el-form-item label="规则状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="备注">
        <el-input type="textarea" :rows="3" v-model="form.remark" style="width:320px"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button v-if="checkBtn('sys:crossStoreSettle:save')" type="primary" @click="save">保存规则</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: false,
      form: this.blankForm()
    }
  },
  computed: {
    ratioSum () {
      return (Number(this.form.buyStoreRatio) || 0) + (Number(this.form.lessonStoreRatio) || 0)
    }
  },
  mounted () {
    this.getInfo()
  },
  methods: {
    blankForm () {
      return {
        id: '',
        ruleName: '跨店结算规则',
        crossStoreEnabled: 0,
        incomeOwnerType: 1,
        buyStoreRatio: 100,
        lessonStoreRatio: 0,
        coachFeeOwnerType: 1,
        status: 1,
        remark: ''
      }
    },
    async getInfo () {
      this.loading = true
      try {
        var res = await this.apis.crossStoreSettle_info({})
        var rule = (res && res.rule) || (res && res.entity) || null
        if (rule && (rule.id || rule.ruleName)) {
          var form = this.blankForm()
          Object.keys(form).forEach(function (key) {
            if (rule[key] !== undefined && rule[key] !== null) form[key] = rule[key]
          })
          form.coachFeeOwnerType = 1
          this.form = form
        }
      } finally {
        this.loading = false
      }
    },
    onOwnerTypeChange (val) {
      var n = Number(val)
      if (n === 1) {
        this.form.buyStoreRatio = 100
        this.form.lessonStoreRatio = 0
      } else if (n === 2) {
        this.form.buyStoreRatio = 0
        this.form.lessonStoreRatio = 100
      }
      // n === 3 保留用户填写值
    },
    onRatioChange () {
      // 联动：改一侧自动补另一侧，保证和为100（用户仍可再手动微调）
      var buy = Number(this.form.buyStoreRatio) || 0
      if (buy >= 0 && buy <= 100) {
        this.form.lessonStoreRatio = 100 - buy
      }
    },
    save () {
      var f = this.form
      if (!f.ruleName) return this.$message.warning('请输入规则名称')
      var n = Number(f.incomeOwnerType)
      if (n === 3) {
        var sum = (Number(f.buyStoreRatio) || 0) + (Number(f.lessonStoreRatio) || 0)
        if (sum !== 100) return this.$message.warning('分成比例合计必须为100%')
      } else if (n === 1) {
        f.buyStoreRatio = 100
        f.lessonStoreRatio = 0
      } else if (n === 2) {
        f.buyStoreRatio = 0
        f.lessonStoreRatio = 100
      }
      f.coachFeeOwnerType = 1
      var data = {
        id: f.id,
        ruleName: f.ruleName,
        crossStoreEnabled: f.crossStoreEnabled,
        incomeOwnerType: f.incomeOwnerType,
        buyStoreRatio: f.buyStoreRatio,
        lessonStoreRatio: f.lessonStoreRatio,
        coachFeeOwnerType: 1,
        status: f.status,
        remark: f.remark
      }
      this.loading = true
      this.apis.crossStoreSettle_save(data).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('保存成功')
          this.getInfo()
        }
      }).catch(() => {}).finally(() => {
        this.loading = false
      })
    },
    reset () {
      this.getInfo()
    }
  }
}
</script>

<style scoped lang="scss">
.cs-wrap {
  padding: 10px 0;
}
.cs-ratio {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.cs-ratio-label {
  margin-right: 8px;
  color: #606266;
}
.cs-unit {
  margin: 0 4px 0 6px;
  color: #909399;
}
.cs-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}
</style>
